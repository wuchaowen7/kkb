package com.ism.base.controller;

import com.ism.common.entity.Warehouse;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.base.service.WarehouseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/base/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/page")
    public Result<PageResult<Warehouse>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Warehouse> page = warehouseService.pageQuery(pageNum, pageSize, keyword);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/list")
    public Result<java.util.List<Warehouse>> list() {
        return Result.ok(warehouseService.list());
    }

    @GetMapping("/{id}")
    public Result<Warehouse> getById(@PathVariable Long id) {
        return Result.ok(warehouseService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Warehouse warehouse) {
        warehouseService.save(warehouse);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        warehouseService.updateById(warehouse);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable String ids) {
        for (String id : ids.split(",")) {
            warehouseService.removeById(Long.parseLong(id));
        }
        return Result.ok();
    }
}
