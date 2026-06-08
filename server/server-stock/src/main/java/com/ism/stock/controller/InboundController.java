package com.ism.stock.controller;

import com.ism.common.entity.InboundOrder;
import com.ism.common.entity.InboundDetail;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.stock.service.InboundService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock/inbound")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService inboundService;

    @GetMapping("/page")
    public Result<PageResult<InboundOrder>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Page<InboundOrder> page = inboundService.pageQuery(pageNum, pageSize, status);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<InboundOrder> getById(@PathVariable Long id) {
        return Result.ok(inboundService.getById(id));
    }

    @GetMapping("/{id}/details")
    public Result<List<InboundDetail>> getDetails(@PathVariable Long id) {
        return Result.ok(inboundService.getDetails(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody InboundOrder order) {
        inboundService.createInbound(order);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        inboundService.confirmInbound(id);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody InboundOrder order) {
        order.setId(id);
        inboundService.updateById(order);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        inboundService.removeById(id);
        return Result.ok();
    }
}
