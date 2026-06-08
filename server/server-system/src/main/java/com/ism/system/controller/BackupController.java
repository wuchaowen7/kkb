package com.ism.system.controller;

import com.ism.common.vo.Result;
import com.ism.system.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getBackupList() {
        return Result.ok(backupService.getBackupList());
    }

    @PostMapping
    public Result<String> createBackup(@RequestBody Map<String, String> params) {
        String remark = params.getOrDefault("remark", "");
        String fileName = backupService.createBackup(remark);
        return Result.ok(fileName);
    }

    @DeleteMapping("/{fileName}")
    public Result<Void> deleteBackup(@PathVariable String fileName) {
        backupService.deleteBackup(fileName);
        return Result.ok();
    }

    @PostMapping("/restore/{fileName}")
    public Result<Void> restoreBackup(@PathVariable String fileName) {
        backupService.restoreBackup(fileName);
        return Result.ok();
    }
}