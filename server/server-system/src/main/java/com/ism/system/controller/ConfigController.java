package com.ism.system.controller;

import com.ism.common.vo.Result;
import com.ism.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    public Result<Map<String, Object>> getConfig() {
        return Result.ok(configService.getConfig());
    }

    @PutMapping
    public Result<Void> updateConfig(@RequestBody Map<String, Object> config) {
        configService.updateConfig(config);
        return Result.ok();
    }
}