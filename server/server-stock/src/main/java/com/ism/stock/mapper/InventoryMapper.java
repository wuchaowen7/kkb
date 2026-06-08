package com.ism.stock.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ism.common.entity.StockInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryMapper extends BaseMapper<StockInventory> {

    List<Map<String, Object>> selectInventoryWithDetails(@Param("productId") Long productId, 
                                                         @Param("warehouseId") Long warehouseId, 
                                                         @Param("keyword") String keyword);

    IPage<Map<String, Object>> selectInventoryPage(IPage<StockInventory> page, 
                                                   @Param("productId") Long productId, 
                                                   @Param("warehouseId") Long warehouseId, 
                                                   @Param("keyword") String keyword);
}
