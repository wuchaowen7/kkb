package com.ism.stock.controller;

import com.ism.common.entity.TransferOrder;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.stock.service.TransferService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/page")
    public Result<PageResult<TransferOrder>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<TransferOrder> page = transferService.pageQuery(pageNum, pageSize);
        return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @PostMapping
    public Result<Void> create(@RequestBody TransferOrder order) {
        transferService.createTransfer(order);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        transferService.confirmTransfer(id);
        return Result.ok();
    }
}
