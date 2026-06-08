package com.ism.intelligence.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ism.common.entity.AlertRecord;
import com.ism.common.vo.PageResult;
import com.ism.common.vo.Result;
import com.ism.intelligence.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/intelligence")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/alerts")
    public Result<PageResult<AlertRecord>> list(
            @RequestParam(name = "alertType", required = false) String alertType,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        List<AlertRecord> allRecords = alertService.listAlerts(alertType, status);
        long total = allRecords.size();
        int offset = (pageNum - 1) * pageSize;
        List<AlertRecord> pageRecords = new ArrayList<>();
        if (offset < total) {
            int end = Math.min(offset + pageSize, allRecords.size());
            pageRecords = new ArrayList<>(allRecords.subList(offset, end));
        }
        return Result.ok(PageResult.of(total, pageRecords));
    }

    @GetMapping("/alerts/count")
    public Result<Map<String, Long>> alertCount() {
        return Result.ok(alertService.getAlertCount());
    }

    @PutMapping("/alerts/{id}/handle")
    public Result<Void> handle(@PathVariable(name = "id") Long id) {
        alertService.handleAlert(id);
        return Result.ok();
    }

    @PutMapping("/alerts/batch-handle")
    public Result<Void> batchHandle(@RequestBody List<Long> ids) {
        alertService.batchHandleAlerts(ids);
        return Result.ok();
    }

    @GetMapping("/predict/{productId}")
    public Result<Map<String, Object>> predict(@PathVariable(name = "productId") Long productId, @RequestParam(name = "days", defaultValue = "7") Integer days) {
        return Result.ok(alertService.getPrediction(productId, days));
    }

    @PostMapping("/replenish/suggest/{productId}")
    public Result<Map<String, Object>> replenishSuggest(@PathVariable(name = "productId") Long productId) {
        return Result.ok(alertService.getReplenishSuggestion(productId));
    }

    @GetMapping("/analysis/top-selling")
    public Result<List<Map<String, Object>>> topSelling(@RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        return Result.ok(alertService.getTopSelling(limit));
    }

    @GetMapping("/analysis/turnover")
    public Result<List<Map<String, Object>>> turnover() {
        return Result.ok(alertService.getTurnoverRate());
    }

    @GetMapping("/analysis/sales-trend")
    public Result<List<Map<String, Object>>> salesTrend(@RequestParam(name = "days", defaultValue = "7") Integer days) {
        return Result.ok(alertService.getSalesTrend(days));
    }

    @GetMapping("/analysis/category")
    public Result<List<Map<String, Object>>> categoryAnalysis() {
        return Result.ok(alertService.getCategoryAnalysis());
    }

    @GetMapping("/analysis/stats")
    public Result<Map<String, Object>> stats() {
        return Result.ok(alertService.getStats());
    }

    @GetMapping("/analysis/alert-stats")
    public Result<Map<String, Long>> alertStats() {
        return Result.ok(alertService.getAlertStats());
    }
}