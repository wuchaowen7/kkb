package com.ism.system.service.impl;

import com.ism.system.service.BackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class BackupServiceImpl implements BackupService {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static final String BACKUP_DIR = "./backup";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Map<String, Object>> getBackupList() {
        List<Map<String, Object>> backups = new ArrayList<>();
        File dir = new File(BACKUP_DIR);
        
        if (!dir.exists()) {
            dir.mkdirs();
            return backups;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
        if (files == null) return backups;

        Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        for (File file : files) {
            Map<String, Object> info = new HashMap<>();
            info.put("fileName", file.getName());
            info.put("size", file.length());
            info.put("createTime", LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(file.lastModified()), 
                java.time.ZoneId.systemDefault()).format(FORMATTER));
            
            // 解析备注信息
            String name = file.getName();
            int idx = name.indexOf("_");
            if (idx > 0) {
                String remark = name.substring(0, idx);
                info.put("remark", remark);
            } else {
                info.put("remark", "自动备份");
            }
            
            backups.add(info);
        }
        return backups;
    }

    @Override
    public String createBackup(String remark) {
        try {
            File dir = new File(BACKUP_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupName = (remark.isEmpty() ? "backup" : remark) + "_" + timestamp + ".sql";
            String backupPath = BACKUP_DIR + "/" + backupName;

            // 简单的SQL导出实现
            StringBuilder sql = new StringBuilder();
            sql.append("-- Backup created at ").append(LocalDateTime.now().format(FORMATTER)).append("\n\n");
            
            // 导出基础数据（简化实现）
            sql.append("-- Base Product\n");
            sql.append("SELECT * FROM base_product;\n\n");
            sql.append("-- Stock Inventory\n");
            sql.append("SELECT * FROM stock_inventory;\n\n");
            sql.append("-- Base Warehouse\n");
            sql.append("SELECT * FROM base_warehouse;\n\n");
            sql.append("-- Base Supplier\n");
            sql.append("SELECT * FROM base_supplier;\n\n");
            sql.append("-- Sys User\n");
            sql.append("SELECT * FROM sys_user;\n\n");

            Files.write(Paths.get(backupPath), sql.toString().getBytes());
            log.info("Backup created: {}", backupPath);
            
            return backupName;
        } catch (IOException e) {
            log.error("Failed to create backup", e);
            throw new RuntimeException("备份失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteBackup(String fileName) {
        try {
            Path path = Paths.get(BACKUP_DIR, fileName);
            Files.deleteIfExists(path);
            log.info("Backup deleted: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to delete backup", e);
            throw new RuntimeException("删除备份失败: " + e.getMessage());
        }
    }

    @Override
    public void restoreBackup(String fileName) {
        try {
            Path path = Paths.get(BACKUP_DIR, fileName);
            if (!Files.exists(path)) {
                throw new RuntimeException("备份文件不存在");
            }
            log.info("Restore backup: {}", fileName);
        } catch (Exception e) {
            log.error("Failed to restore backup", e);
            throw new RuntimeException("恢复备份失败: " + e.getMessage());
        }
    }
}