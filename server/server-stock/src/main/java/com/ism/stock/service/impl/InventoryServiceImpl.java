package com.ism.stock.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ism.common.entity.StockInventory;
import com.ism.stock.mapper.InventoryMapper;
import com.ism.stock.service.InventoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, StockInventory> implements InventoryService {

    @Override
    public IPage<Map<String, Object>> pageQuery(Integer pageNum, Integer pageSize, Long productId, Long warehouseId, String keyword) {
        return ((InventoryMapper) baseMapper).selectInventoryPage(new Page<>(pageNum, pageSize), productId, warehouseId, keyword);
    }

    @Override
    public List<Map<String, Object>> listWithDetails(Long productId, Long warehouseId, String keyword) {
        return ((InventoryMapper) baseMapper).selectInventoryWithDetails(productId, warehouseId, keyword);
    }
}