package com.ism.system.service;

import java.util.List;
import java.util.Map;

public interface BackupService {

    List<Map<String, Object>> getBackupList();

    String createBackup(String remark);

    void deleteBackup(String fileName);

    void restoreBackup(String fileName);
}