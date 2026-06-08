package com.ism.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.StockReturn;
import com.ism.stock.mapper.ReturnMapper;
import com.ism.stock.service.ReturnService;
import org.springframework.stereotype.Service;

@Service
public class ReturnServiceImpl extends ServiceImpl<ReturnMapper, StockReturn> implements ReturnService {

    @Override
    public Page<StockReturn> pageQuery(Integer pageNum, Integer pageSize, String returnNo, String status, Long warehouseId) {
        LambdaQueryWrapper<StockReturn> wrapper = new LambdaQueryWrapper<>();
        if (returnNo != null && !returnNo.isEmpty()) {
            wrapper.like(StockReturn::getReturnNo, returnNo);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(StockReturn::getStatus, status);
        }
        if (warehouseId != null && warehouseId > 0) {
            wrapper.eq(StockReturn::getWarehouseId, warehouseId);
        }
        wrapper.orderByDesc(StockReturn::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
}