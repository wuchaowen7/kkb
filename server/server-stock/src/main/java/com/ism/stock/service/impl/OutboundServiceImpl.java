package com.ism.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.*;
import com.ism.common.exception.BusinessException;
import com.ism.stock.mapper.*;
import com.ism.stock.service.OutboundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundServiceImpl extends ServiceImpl<OutboundMapper, OutboundOrder> implements OutboundService {

    private final OutboundDetailMapper outboundDetailMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final org.redisson.api.RedissonClient redissonClient;

    @Override
    public Page<OutboundOrder> pageQuery(Integer pageNum, Integer pageSize, String status) {
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(OutboundOrder::getStatus, status);
        }
        wrapper.orderByDesc(OutboundOrder::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<OutboundDetail> getDetails(Long outboundId) {
        return outboundDetailMapper.selectList(
            new LambdaQueryWrapper<OutboundDetail>().eq(OutboundDetail::getOutboundId, outboundId));
    }

    @Override
    @Transactional
    public void createOutbound(OutboundOrder order) {
        order.setOutboundNo("OUT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        order.setStatus("PENDING_AUDIT");
        this.save(order);
    }

    /**
     * 出库审核：通过后扣减库存（乐观锁 + Redis分布式锁双层保障）
     */
    @Override
    @Transactional
    public void auditOutbound(Long id, Boolean approved, String opinion) {
        OutboundOrder order = this.getById(id);
        if (order == null || !"PENDING_AUDIT".equals(order.getStatus())) {
            throw new BusinessException("单据状态不正确，无法审核");
        }

        if (!approved) {
            order.setStatus("REJECTED");
            order.setAuditOpinion(opinion);
            order.setAuditTime(LocalDateTime.now());
            this.updateById(order);
            return;
        }

        // 审核通过，扣减库存
        List<OutboundDetail> details = getDetails(id);
        for (OutboundDetail detail : details) {
            deductStock(order.getWarehouseId(), detail.getProductId(), detail.getQuantity(), order.getOutboundNo());
        }

        order.setStatus("AUDITED");
        order.setAuditOpinion(opinion);
        order.setAuditTime(LocalDateTime.now());
        this.updateById(order);
    }

    /**
     * 库存扣减：乐观锁重试（最多3次）+ Redis分布式锁
     */
    private void deductStock(Long warehouseId, Long productId, int quantity, String bizNo) {
        String lockKey = "stock:lock:" + productId + ":" + warehouseId;
        var lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();
            int maxRetry = 3;
            for (int i = 0; i < maxRetry; i++) {
                LambdaQueryWrapper<StockInventory> wrapper = new LambdaQueryWrapper<StockInventory>()
                        .eq(StockInventory::getProductId, productId)
                        .eq(StockInventory::getWarehouseId, warehouseId)
                        .ge(StockInventory::getQuantity, quantity);
                StockInventory inventory = inventoryMapper.selectOne(wrapper);
                if (inventory == null) {
                    throw new BusinessException("库存不足，商品ID: " + productId);
                }

                int beforeQty = inventory.getQuantity();
                // 乐观锁扣减
                inventory.setQuantity(beforeQty - quantity);
                int rows = inventoryMapper.updateById(inventory);
                if (rows > 0) {
                    // 写库存流水
                    InventoryLog log = new InventoryLog();
                    log.setProductId(productId);
                    log.setWarehouseId(warehouseId);
                    log.setBizType("OUTBOUND");
                    log.setBizNo(bizNo);
                    log.setChangeQty(-quantity);
                    log.setBeforeQty(beforeQty);
                    log.setAfterQty(beforeQty - quantity);
                    log.setRemark("销售/领用出库");
                    inventoryLogMapper.insert(log);
                    return;
                }
                log.warn("库存扣减乐观锁冲突，重试 {}/{}", i + 1, maxRetry);
            }
            throw new BusinessException("库存扣减失败，请重试");
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public void updateOutbound(Long id, OutboundOrder order) {
        OutboundOrder existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("出库单不存在");
        }
        if (!"DRAFT".equals(existing.getStatus()) && !"REJECTED".equals(existing.getStatus())) {
            throw new BusinessException("只有草稿或已驳回状态的单据才能修改");
        }
        
        order.setId(id);
        this.updateById(order);
    }

    @Override
    @Transactional
    public void deleteOutbound(Long id) {
        OutboundOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("出库单不存在");
        }
        if (!"DRAFT".equals(order.getStatus())) {
            throw new BusinessException("只有草稿状态的单据才能删除");
        }
        
        this.removeById(id);
    }
}
