@echo off
setlocal
cd /d "%~dp0"

title Smart Campus Management System - H2 In-Memory Mode
echo ========================================================
echo       SMART CAMPUS MANAGEMENT SYSTEM (H2 IN-MEMORY)
echo       Zero MySQL configuration required!
echo ========================================================
echo.

where java >nul 2>nul
if errorlevel 1 goto no_java

if not exist "%~dp0target\smart-campus-1.0.0.jar" goto no_jar

echo Starting Smart Campus in H2 In-Memory Mode...
echo Open http://localhost:8080/ after startup.
echo.
java -jar "%~dp0target\smart-campus-1.0.0.jar" --spring.profiles.active=h2
if errorlevel 1 goto startup_failed

echo.
echo Server stopped normally.
pause
exit /b 0

:no_java
echo [ERROR] Java is not installed or not available in PATH.
echo Install JDK 21 or newer, then try again.
pause
exit /b 1

:no_jar
echo [ERROR] Application JAR not found:
echo %~dp0target\smart-campus-1.0.0.jar
echo Build it first from Command Prompt:
echo .\mvnw.cmd clean package -DskipTests
pause
exit /b 1

:startup_failed
echo.
echo Server stopped because of an error. Review the message above.
pause
exit /b 1
