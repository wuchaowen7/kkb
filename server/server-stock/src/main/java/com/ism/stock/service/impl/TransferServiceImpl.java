package com.ism.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.*;
import com.ism.common.exception.BusinessException;
import com.ism.stock.mapper.*;
import com.ism.stock.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl extends ServiceImpl<TransferMapper, TransferOrder> implements TransferService {

    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;

    @Override
    public Page<TransferOrder> pageQuery(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TransferOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TransferOrder::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createTransfer(TransferOrder order) {
        order.setTransferNo("TR" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        order.setStatus("PENDING");
        this.save(order);
    }

    @Override
    @Transactional
    public void confirmTransfer(Long id) {
        TransferOrder order = this.getById(id);
        if (order == null || !"PENDING".equals(order.getStatus())) {
            throw new BusinessException("调拨单状态不正确");
        }

        // 从源仓库扣减
        LambdaQueryWrapper<StockInventory> fromWrapper = new LambdaQueryWrapper<StockInventory>()
                .eq(StockInventory::getProductId, order.getProductId())
                .eq(StockInventory::getWarehouseId, order.getFromWarehouseId())
                .ge(StockInventory::getQuantity, order.getQuantity());
        StockInventory fromInv = inventoryMapper.selectOne(fromWrapper);
        if (fromInv == null) {
            throw new BusinessException("源仓库库存不足");
        }
        fromInv.setQuantity(fromInv.getQuantity() - order.getQuantity());
        inventoryMapper.updateById(fromInv);

        // 向目标仓库增加
        LambdaQueryWrapper<StockInventory> toWrapper = new LambdaQueryWrapper<StockInventory>()
                .eq(StockInventory::getProductId, order.getProductId())
                .eq(StockInventory::getWarehouseId, order.getToWarehouseId());
        StockInventory toInv = inventoryMapper.selectOne(toWrapper);
        if (toInv == null) {
            toInv = new StockInventory();
            toInv.setProductId(order.getProductId());
            toInv.setWarehouseId(order.getToWarehouseId());
            toInv.setQuantity(order.getQuantity());
            toInv.setLockedQuantity(0);
            toInv.setVersion(1);
            inventoryMapper.insert(toInv);
        } else {
            toInv.setQuantity(toInv.getQuantity() + order.getQuantity());
            inventoryMapper.updateById(toInv);
        }

        // 记录流水
        InventoryLog log = new InventoryLog();
        log.setProductId(order.getProductId());
        log.setWarehouseId(order.getFromWarehouseId());
        log.setBizType("TRANSFER");
        log.setBizNo(order.getTransferNo());
        log.setChangeQty(-order.getQuantity());
        log.setBeforeQty(fromInv.getQuantity() + order.getQuantity());
        log.setAfterQty(fromInv.getQuantity());
        log.setRemark("调拨至仓库" + order.getToWarehouseId());
        inventoryLogMapper.insert(log);

        order.setStatus("CONFIRMED");
        this.updateById(order);
    }
}
