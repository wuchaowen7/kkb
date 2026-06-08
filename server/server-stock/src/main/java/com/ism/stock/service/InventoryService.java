package com.ism.stock.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ism.common.entity.StockInventory;

import java.util.List;
import java.util.Map;

public interface InventoryService extends IService<StockInventory> {
    
    IPage<Map<String, Object>> pageQuery(Integer pageNum, Integer pageSize, Long productId, Long warehouseId, String keyword);
    
    List<Map<String, Object>> listWithDetails(Long productId, Long warehouseId, String keyword);
}