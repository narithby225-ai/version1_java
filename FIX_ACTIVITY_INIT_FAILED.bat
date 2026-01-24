@echo off
COLOR 0C
echo.
echo ========================================
echo   FIX: Activity Initialization Failed
echo ========================================
echo.
echo This error means the layout has Material
echo components that are causing crashes.
echo.
echo I've removed ALL Material components and
echo replaced with simple standard Android views.
echo.
echo This will fix the "Activity initialization
echo failed" error completely.
echo.
pause
echo.

COLOR 0E
echo [STEP 1/4] Uninstalling old version...
adb uninstall com.narith.aims
echo.

echo [STEP 2/4] Cleaning build...
call gradlew clean
if %ERRORLEVEL% NEQ 0 (
    COLOR 0C
    echo ERROR: Clean failed!
    pause
    exit /b 1
)
echo.

echo [STEP 3/4] Building new APK...
echo (This may take 1-2 minutes...)
call gradlew assembleDebug
if %ERRORLEVEL% NEQ 0 (
    COLOR 0C
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo.

echo [STEP 4/4] Installing...
call gradlew installDebug
if %ERRORLEVEL% NEQ 0 (
    COLOR 0C
    echo ERROR: Install failed!
    echo Make sure device is connected.
    pause
    exit /b 1
)
echo.

COLOR 0A
echo ========================================
echo   SUCCESS! App Installed
echo ========================================
echo.
echo The "Activity initialization failed"
echo error should now be FIXED!
echo.
echo Launch the app on your device now.
echo.
echo What you should see:
echo  1. Splash screen (1.5 seconds)
echo  2. Login screen with simple design
echo  3. Username and password fields
echo  4. Sign In button
echo.
echo Test accounts:
echo  - admin / 1234
echo  - user / 1234
echo.
pause

echo.
echo Launch app automatically? (Y/N)
set /p LAUNCH=
if /i "%LAUNCH%"=="Y" (
    echo.
    echo Launching...
    adb shell am start -n com.narith.aims/.view.SplashActivity
    echo.
    echo App launched! Check your device.
)

echo.
pause
