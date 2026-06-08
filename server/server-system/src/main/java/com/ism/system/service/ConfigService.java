package com.ism.system.service;

import java.util.Map;

public interface ConfigService {

    Map<String, Object> getConfig();

    void updateConfig(Map<String, Object> config);
}