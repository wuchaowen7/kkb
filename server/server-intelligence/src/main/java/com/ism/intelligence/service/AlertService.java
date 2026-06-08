package com.ism.intelligence.service;

import com.ism.common.entity.AlertRecord;
import java.util.List;
import java.util.Map;

public interface AlertService {
    List<AlertRecord> listAlerts(String alertType, Integer status);
    Map<String, Long> getAlertCount();
    void handleAlert(Long id);
    void batchHandleAlerts(List<Long> ids);
    Map<String, Object> getPrediction(Long productId, Integer days);
    Map<String, Object> getReplenishSuggestion(Long productId);
    List<Map<String, Object>> getTopSelling(Integer limit);
    List<Map<String, Object>> getTurnoverRate();
    List<Map<String, Object>> getSalesTrend(Integer days);
    List<Map<String, Object>> getCategoryAnalysis();
    Map<String, Object> getStats();
    Map<String, Long> getAlertStats();
}
