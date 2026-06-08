@echo off
chcp 65001 > nul
mysql -u root -p123456 --default-character-set=utf8mb4 < docker/mysql/init/init.sql
echo Database initialized successfully!