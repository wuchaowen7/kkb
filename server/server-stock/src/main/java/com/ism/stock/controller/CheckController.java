package com.ism.stock.controller;

import com.ism.common.entity.CheckOrder;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.stock.service.CheckService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock/check")
@RequiredArgsConstructor
public class CheckController {

    private final CheckService checkService;

    @GetMapping("/page")
    public Result<PageResult<CheckOrder>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CheckOrder> page = checkService.pageQuery(pageNum, pageSize);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @PostMapping
    public Result<Void> create(@RequestBody CheckOrder order) {
        checkService.createCheck(order);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        checkService.confirmCheck(id);
        return Result.ok();
    }
}
