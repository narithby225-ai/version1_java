@echo off
echo ========================================
echo AIMS App Build Script
echo ========================================
echo.

REM Check if google-services.json exists
if not exist "app\google-services.json" (
    echo [ERROR] google-services.json NOT FOUND!
    echo.
    echo Please download google-services.json from Firebase Console and place it in the app\ folder
    echo.
    echo Steps:
    echo 1. Go to https://console.firebase.google.com/
    echo 2. Select your project
    echo 3. Add Android app with package: com.narith.aims
    echo 4. Download google-services.json
    echo 5. Place it in: app\google-services.json
    echo.
    pause
    exit /b 1
)

echo [OK] google-services.json found!
echo.

REM Check if gradlew exists
if not exist "gradlew.bat" (
    echo [INFO] Gradle wrapper not found. Creating wrapper...
    gradle wrapper
    echo.
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
) else (
    echo ========================================
    echo [ERROR] Build failed! Check errors above.
    echo ========================================
)

echo.
pause
