@echo off
echo ========================================
echo AIMS - Build and Run Script
echo ========================================
echo.

echo [1/4] Cleaning previous builds...
call gradlew clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)
echo Clean completed successfully!
echo.

echo [2/4] Building debug APK...
call gradlew assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    echo.
    echo Common issues:
    echo - JAVA_HOME not set correctly
    echo - Gradle sync needed
    echo - Missing dependencies
    echo.
    pause
    exit /b 1
)
echo Build completed successfully!
echo.

echo [3/4] Checking for connected devices...
adb devices
echo.

echo [4/4] Installing APK on device...
call gradlew installDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Installation failed!
    echo Make sure a device is connected or emulator is running.
    pause
    exit /b 1
)
echo.

echo ========================================
echo SUCCESS! App installed successfully!
echo ========================================
echo.
echo The app should now be on your device.
echo Look for "AIMS" icon to launch it.
echo.
pause
