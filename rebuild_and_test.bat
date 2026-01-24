@echo off
echo ========================================
echo AIMS - Rebuild and Test (Crash Fix)
echo ========================================
echo.

echo [1/5] Uninstalling old version...
adb uninstall com.narith.aims
echo.

echo [2/5] Cleaning previous builds...
call gradlew clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)
echo.

echo [3/5] Building new APK with crash fix...
call gradlew assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo.

echo [4/5] Installing new version...
call gradlew installDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Installation failed!
    echo Make sure device is connected.
    pause
    exit /b 1
)
echo.

echo [5/5] Launching app...
adb shell am start -n com.narith.aims/.view.SplashActivity
echo.

echo ========================================
echo SUCCESS! App installed and launched!
echo ========================================
echo.
echo Watch your device for the splash screen.
echo It should transition to login after 2 seconds.
echo.
echo If it crashes, check logcat:
echo   adb logcat ^| findstr "AndroidRuntime"
echo.
pause
