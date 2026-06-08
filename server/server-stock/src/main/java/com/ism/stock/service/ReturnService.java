package com.ism.stock.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.StockReturn;

public interface ReturnService extends IService<StockReturn> {
    
    Page<StockReturn> pageQuery(Integer pageNum, Integer pageSize, String returnNo, String status, Long warehouseId);
}