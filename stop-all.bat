@echo off
echo ============================================
echo Intelligent Inventory Management System - Restart
echo ============================================

set BASE_DIR=%~dp0server
set WEB_DIR=%~dp0web

echo.
echo [1/2] Stopping all services...

:: 使用 PowerShell 停止本项目的 Java 服务（通过命令行匹配 jar 文件路径）
powershell -Command "Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*server-system*' } | Stop-Process -Force -ErrorAction SilentlyContinue"
powershell -Command "Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*server-base*' } | Stop-Process -Force -ErrorAction SilentlyContinue"
powershell -Command "Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*server-stock*' } | Stop-Process -Force -ErrorAction SilentlyContinue"
powershell -Command "Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*server-intelligence*' } | Stop-Process -Force -ErrorAction SilentlyContinue"
powershell -Command "Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*server-report*' } | Stop-Process -Force -ErrorAction SilentlyContinue"
powershell -Command "Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*server-gateway*' } | Stop-Process -Force -ErrorAction SilentlyContinue"

:: 停止前端 node 进程
powershell -Command "Get-Process node -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*vite*' } | Stop-Process -Force -ErrorAction SilentlyContinue"

echo Waiting for services to stop...
timeout /t 3 /nobreak >nul

echo.
echo [2/2] Starting all services...

echo.
echo [1/6] Starting server-system (port 8081)...
start "server-system" java -jar %BASE_DIR%\server-system\target\server-system-1.0.0.jar
timeout /t 8 /nobreak >nul

echo.
echo [2/6] Starting server-base (port 8082)...
start "server-base" java -jar %BASE_DIR%\server-base\target\server-base-1.0.0.jar
timeout /t 8 /nobreak >nul

echo.
echo [3/6] Starting server-stock (port 8083)...
start "server-stock" java -jar %BASE_DIR%\server-stock\target\server-stock-1.0.0.jar
timeout /t 8 /nobreak >nul

echo.
echo [4/6] Starting server-intelligence (port 8084)...
start "server-intelligence" java -jar %BASE_DIR%\server-intelligence\target\server-intelligence-1.0.0.jar
timeout /t 8 /nobreak >nul

echo.
echo [5/6] Starting server-report (port 8085)...
start "server-report" java -jar %BASE_DIR%\server-report\target\server-report-1.0.0.jar
timeout /t 8 /nobreak >nul

echo.
echo [6/6] Starting server-gateway (port 8080)...
start "server-gateway" java -jar %BASE_DIR%\server-gateway\target\server-gateway-1.0.0.jar
timeout /t 5 /nobreak >nul

echo.
echo [7/7] Starting frontend (port 5173)...
start "frontend" cmd /k "cd %WEB_DIR% && npm run dev"
timeout /t 5 /nobreak >nul

echo.
echo ============================================
echo All services restarted!
echo Frontend:    http://localhost:5173
echo Gateway:     http://localhost:8080
echo ============================================
