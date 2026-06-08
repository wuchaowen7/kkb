package com.ism.report.service;

import java.util.Map;

public interface ReportService {
    Map<String, Object> getDashboardData();
    Map<String, Object> getInOutStockReport(String startDate, String endDate);
    Map<String, Object> getInventoryDetail(Map<String, Object> params);
    String exportToExcel(String type);
}
