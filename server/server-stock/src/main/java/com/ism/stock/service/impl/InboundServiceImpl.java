package com.ism.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.base.mapper.ProductMapper;
import com.ism.common.entity.*;
import com.ism.common.exception.BusinessException;
import com.ism.stock.mapper.*;
import com.ism.stock.service.InboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InboundServiceImpl extends ServiceImpl<InboundMapper, InboundOrder> implements InboundService {

    private final InboundDetailMapper inboundDetailMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final ProductMapper productMapper;

    @Override
    public Page<InboundOrder> pageQuery(Integer pageNum, Integer pageSize, String status) {
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(InboundOrder::getStatus, status);
        }
        wrapper.orderByDesc(InboundOrder::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<InboundDetail> getDetails(Long inboundId) {
        return inboundDetailMapper.selectList(new LambdaQueryWrapper<InboundDetail>().eq(InboundDetail::getInboundId, inboundId));
    }

    @Override
    @Transactional
    public void createInbound(InboundOrder order) {
        order.setInboundNo("IN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        order.setStatus("DRAFT");
        this.save(order);
        
        // 保存明细
        if (order.getDetails() != null && !order.getDetails().isEmpty()) {
            for (InboundDetail detail : order.getDetails()) {
                if (detail.getProductId() != null && detail.getQuantity() != null && detail.getQuantity() > 0) {
                    detail.setInboundId(order.getId());
                    inboundDetailMapper.insert(detail);
                }
            }
        }
    }

    @Override
    @Transactional
    public void confirmInbound(Long id) {
        InboundOrder order = this.getById(id);
        if (order == null || !"DRAFT".equals(order.getStatus())) {
            throw new BusinessException("单据状态不正确");
        }
        order.setStatus("CONFIRMED");
        this.updateById(order);

        // 增加库存并记录流水
        List<InboundDetail> details = getDetails(id);
        for (InboundDetail detail : details) {
            // 查询或创建库存记录
            LambdaQueryWrapper<StockInventory> invWrapper = new LambdaQueryWrapper<StockInventory>()
                    .eq(StockInventory::getProductId, detail.getProductId())
                    .eq(StockInventory::getWarehouseId, order.getWarehouseId())
                    .eq(StockInventory::getBatchNo, detail.getBatchNo());
            StockInventory inventory = inventoryMapper.selectOne(invWrapper);

            if (inventory == null) {
                inventory = new StockInventory();
                inventory.setProductId(detail.getProductId());
                inventory.setWarehouseId(order.getWarehouseId());
                inventory.setBatchNo(detail.getBatchNo());
                inventory.setQuantity(detail.getQuantity());
                inventory.setLockedQuantity(0);
                inventory.setVersion(1);
                inventory.setProductionDate(detail.getProductionDate());
                inventory.setExpiryDate(detail.getExpiryDate());
                inventoryMapper.insert(inventory);
                
                // 同步更新商品表的生产日期（使用最新入库批次的生产日期）
                if (detail.getProductionDate() != null) {
                    Product product = productMapper.selectById(detail.getProductId());
                    if (product != null) {
                        product.setProductionDate(detail.getProductionDate());
                        productMapper.updateById(product);
                    }
                }
            } else {
                // 乐观锁更新
                inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                inventoryMapper.updateById(inventory);
                
                // 如果新批次的生产日期更新，则同步更新商品表
                if (detail.getProductionDate() != null) {
                    Product product = productMapper.selectById(detail.getProductId());
                    if (product != null && (product.getProductionDate() == null || 
                        detail.getProductionDate().isAfter(product.getProductionDate()))) {
                        product.setProductionDate(detail.getProductionDate());
                        productMapper.updateById(product);
                    }
                }
            }

            // 写库存流水
            InventoryLog log = new InventoryLog();
            log.setProductId(detail.getProductId());
            log.setWarehouseId(order.getWarehouseId());
            log.setBizType("INBOUND");
            log.setBizNo(order.getInboundNo());
            log.setChangeQty(detail.getQuantity());
            log.setBeforeQty(inventory.getQuantity() - detail.getQuantity());
            log.setAfterQty(inventory.getQuantity());
            log.setRemark("采购入库");
            inventoryLogMapper.insert(log);
        }
    }
}
