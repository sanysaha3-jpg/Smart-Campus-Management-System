@echo off
setlocal
cd /d "%~dp0"
if exist "smart-campus\target\smart-campus-1.0.0.jar" (
    cd smart-campus
)

title Smart Campus Management System - MySQL Mode
echo ========================================================
echo       SMART CAMPUS MANAGEMENT SYSTEM (MySQL Mode)
echo ========================================================
echo.

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH!
    echo Please install JDK 17 or higher (JDK 21/22 recommended).
    pause
    exit /b 1
)

echo [1/2] Launching browser waiter...
start /b cmd /c "powershell -NoProfile -Command ""for ($i=0; $i -lt 35; $i++) { try { $res = (Invoke-WebRequest -Uri 'http://localhost:8080/' -UseBasicParsing -TimeoutSec 2).StatusCode; if ($res -eq 200) { Write-Host 'Server is live! Opening browser...'; Start-Process 'http://localhost:8080/'; break } } catch {} Start-Sleep -Seconds 1 }"""

echo [2/2] Starting Smart Campus Server on http://localhost:8080 ...
echo (The browser will open automatically once the server is ready)
echo.
java -jar target\smart-campus-1.0.0.jar
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [NOTICE] If MySQL is not running, run 'run-h2.bat' instead for zero-config mode!
    pause
)
