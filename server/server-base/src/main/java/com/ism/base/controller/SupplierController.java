package com.ism.base.controller;

import com.ism.common.entity.Supplier;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.base.service.SupplierService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/base/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/page")
    public Result<PageResult<Supplier>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Supplier> page = supplierService.pageQuery(pageNum, pageSize, keyword);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/list")
    public Result<java.util.List<Supplier>> list() {
        return Result.ok(supplierService.list());
    }

    @GetMapping("/{id}")
    public Result<Supplier> getById(@PathVariable Long id) {
        return Result.ok(supplierService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Supplier supplier) {
        supplierService.save(supplier);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierService.updateById(supplier);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable String ids) {
        for (String id : ids.split(",")) {
            supplierService.removeById(Long.parseLong(id));
        }
        return Result.ok();
    }
}
