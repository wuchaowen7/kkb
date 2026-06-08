package com.ism.stock.controller;

import com.ism.common.entity.StockReturn;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.stock.service.ReturnService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock/return")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @GetMapping("/page")
    public Result<PageResult<StockReturn>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String returnNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long warehouseId) {
        Page<StockReturn> page = returnService.pageQuery(pageNum, pageSize, returnNo, status, warehouseId);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<StockReturn> getById(@PathVariable Long id) {
        return Result.ok(returnService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody StockReturn stockReturn) {
        returnService.save(stockReturn);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StockReturn stockReturn) {
        stockReturn.setId(id);
        returnService.updateById(stockReturn);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        returnService.removeById(id);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        StockReturn stockReturn = returnService.getById(id);
        if (stockReturn != null) {
            stockReturn.setStatus("CONFIRMED");
            returnService.updateById(stockReturn);
        }
        return Result.ok();
    }
}