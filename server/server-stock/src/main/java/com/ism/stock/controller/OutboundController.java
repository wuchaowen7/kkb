package com.ism.stock.controller;

import com.ism.common.entity.OutboundOrder;
import com.ism.common.entity.OutboundDetail;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.stock.service.OutboundService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock/outbound")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundService outboundService;

    @GetMapping("/page")
    public Result<PageResult<OutboundOrder>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Page<OutboundOrder> page = outboundService.pageQuery(pageNum, pageSize, status);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<OutboundOrder> getById(@PathVariable Long id) {
        return Result.ok(outboundService.getById(id));
    }

    @GetMapping("/{id}/details")
    public Result<List<OutboundDetail>> getDetails(@PathVariable Long id) {
        return Result.ok(outboundService.getDetails(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody OutboundOrder order) {
        outboundService.createOutbound(order);
        return Result.ok();
    }

    @PutMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Boolean approved, @RequestParam(required = false) String opinion) {
        outboundService.auditOutbound(id, approved, opinion);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody OutboundOrder order) {
        outboundService.updateOutbound(id, order);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        outboundService.deleteOutbound(id);
        return Result.ok();
    }
}
