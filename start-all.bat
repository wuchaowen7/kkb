@echo off
echo ============================================
echo Intelligent Inventory Management System - Start
echo ============================================

set BASE_DIR=%~dp0server
set WEB_DIR=%~dp0web

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
echo All services started!
echo Frontend:    http://localhost:5173
echo Gateway:     http://localhost:8080
echo ============================================
