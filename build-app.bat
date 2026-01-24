@echo off
echo ========================================
echo AIMS App - SQLite Version Build Script
echo ========================================
echo.

echo [INFO] This app now uses SQLite (no Firebase needed!)
echo.

REM Check if gradlew exists
if not exist "gradlew.bat" (
    echo [ERROR] Gradle wrapper not found!
    echo Please run this from the project root directory.
    pause
    exit /b 1
)

echo [INFO] Cleaning project...
call gradlew.bat clean
echo.

echo [INFO] Building project...
call gradlew.bat build
echo.

if %ERRORLEVEL% EQU 0 (
    echo ========================================
    echo [SUCCESS] Build completed successfully!
    echo ========================================
    echo.
    echo You can now:
    echo 1. Install the APK on your device
    echo 2. Run from Android Studio
    echo.
    echo Default login:
    echo Email: admin@aims.com
    echo Password: admin123
    echo.
) else (
    echo ========================================
    echo [ERROR] Build failed! Check errors above.
    echo ========================================
)

echo.
pause
