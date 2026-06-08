package com.ism.stock.controller;

import com.ism.common.entity.StockInventory;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.stock.service.InventoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String keyword) {
        IPage<Map<String, Object>> page = inventoryService.pageQuery(pageNum, pageSize, productId, warehouseId, keyword);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String keyword) {
        return Result.ok(inventoryService.listWithDetails(productId, warehouseId, keyword));
    }

    @GetMapping("/{id}")
    public Result<StockInventory> getById(@PathVariable Long id) {
        return Result.ok(inventoryService.getById(id));
    }
}