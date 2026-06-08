package com.ism.report.controller;

import com.ism.common.vo.Result;
import com.ism.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.ok(reportService.getDashboardData());
    }

    @GetMapping("/in-out-stock")
    public Result<Map<String, Object>> inOutStock(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.ok(reportService.getInOutStockReport(startDate, endDate));
    }

    @GetMapping("/inventory-detail")
    public Result<Map<String, Object>> inventoryDetail(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("productId", productId);
        params.put("warehouseId", warehouseId);
        params.put("categoryId", categoryId);
        params.put("productCode", productCode);
        params.put("productName", productName);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("sortField", sortField);
        params.put("sortOrder", sortOrder);
        return Result.ok(reportService.getInventoryDetail(params));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportExcel(@RequestParam String type) {
        String filePath = reportService.exportToExcel(type);
        
        if (filePath == null) {
            return ResponseEntity.notFound().build();
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(file);
        
        String fileName = "inventory_detail_" + System.currentTimeMillis() + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}
