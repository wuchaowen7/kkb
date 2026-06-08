package com.ism.system.service.impl;

import com.ism.system.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Value("${app.name:Smart Inventory}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    private Map<String, Object> systemConfig = new HashMap<>();

    public ConfigServiceImpl() {
        // 默认配置
        systemConfig.put("appName", "Smart Inventory");
        systemConfig.put("appVersion", "1.0.0");
        systemConfig.put("companyName", "ISM Corporation");
        systemConfig.put("copyright", "2024 ISM. All rights reserved.");
        systemConfig.put("autoBackup", true);
        systemConfig.put("backupTime", "02:00");
        systemConfig.put("alertThreshold", 10);
        systemConfig.put("expiryWarningDays", 30);
        systemConfig.put("sessionTimeout", 30);
        systemConfig.put("language", "zh-CN");
        systemConfig.put("theme", "default");
    }

    @Override
    public Map<String, Object> getConfig() {
        return new HashMap<>(systemConfig);
    }

    @Override
    public void updateConfig(Map<String, Object> config) {
        systemConfig.putAll(config);
        log.info("System config updated: {}", config);
    }
}