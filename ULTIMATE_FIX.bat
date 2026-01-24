@echo off
COLOR 0A
echo.
echo ========================================
echo    AIMS - ULTIMATE CRASH FIX
echo ========================================
echo.
echo This script will:
echo  1. Completely uninstall old version
echo  2. Clean all build caches
echo  3. Rebuild from scratch
echo  4. Install fresh version
echo  5. Launch with monitoring
echo.
pause
echo.

echo [STEP 1/5] Uninstalling old version...
echo ----------------------------------------
adb uninstall com.narith.aims
if %ERRORLEVEL% EQU 0 (
    echo SUCCESS: Old version uninstalled
) else (
    echo WARNING: App may not have been installed
)
echo.
timeout /t 2 >nul

echo [STEP 2/5] Cleaning build caches...
echo ----------------------------------------
call gradlew clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed!
    echo Check if Gradle is working properly.
    pause
    exit /b 1
)
echo SUCCESS: Build cache cleaned
echo.
timeout /t 2 >nul

echo [STEP 3/5] Building fresh APK...
echo ----------------------------------------
echo This may take a few minutes...
call gradlew assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    echo.
    echo Common issues:
    echo  - JAVA_HOME not set correctly
    echo  - Internet connection needed for dependencies
    echo  - Gradle sync required
    echo.
    pause
    exit /b 1
)
echo SUCCESS: APK built successfully
echo.
timeout /t 2 >nul

echo [STEP 4/5] Checking device connection...
echo ----------------------------------------
adb devices
echo.
echo Make sure your device is listed above.
echo If not, check USB debugging is enabled.
echo.
timeout /t 3 >nul

echo [STEP 5/5] Installing new version...
echo ----------------------------------------
call gradlew installDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Installation failed!
    echo.
    echo Make sure:
    echo  - Device is connected
    echo  - USB debugging is enabled
    echo  - Device is unlocked
    echo.
    pause
    exit /b 1
)
echo SUCCESS: App installed
echo.
timeout /t 2 >nul

echo ========================================
echo    INSTALLATION COMPLETE!
echo ========================================
echo.
echo The app is now installed on your device.
echo.
echo NEXT STEPS:
echo  1. Look at your device
echo  2. Find the "AIMS" app icon
echo  3. Tap to launch
echo.
echo OR launch automatically:
echo.
set /p LAUNCH="Launch app now? (Y/N): "
if /i "%LAUNCH%"=="Y" (
    echo.
    echo Launching app...
    adb shell am start -n com.narith.aims/.view.SplashActivity
    echo.
    echo App launched! Check your device.
    echo.
    echo MONITORING LOGS...
    echo Press Ctrl+C to stop monitoring
    echo ----------------------------------------
    adb logcat -c
    adb logcat | findstr "SplashActivity LoginActivity AndroidRuntime FATAL"
)

echo.
echo ========================================
echo    DONE!
echo ========================================
echo.
echo If the app crashes:
echo  1. Check FINAL_CRASH_FIX.md
echo  2. Run: adb logcat ^> crash.txt
echo  3. Check crash.txt for errors
echo.
pause
