# Splash Screen Fix Guide

## Changes Made to Fix Startup Issues

### 1. Removed MaterialCardView Dependency
- Changed from `MaterialCardView` to simple `FrameLayout`
- Created `bg_circle_white.xml` drawable for circular background
- This eliminates potential Material library conflicts

### 2. Added Error Handling
- Wrapped all view initialization in try-catch block
- If splash fails, app goes directly to LoginActivity
- Prevents app from crashing on startup

### 3. Null Safety Checks
- Added null checks before animating each view
- Ensures animations only run if views are found
- Prevents NullPointerException crashes

### 4. Simplified Layout
- Used standard Android views (FrameLayout, LinearLayout, RelativeLayout)
- Removed Material-specific attributes
- More compatible across different Android versions

## Files Modified

1. **SplashActivity.java**
   - Added try-catch error handling
   - Added null checks for all views
   - Changed logoCard to logoContainer
   - Removed MaterialCardView import

2. **activity_splash.xml**
   - Replaced MaterialCardView with FrameLayout
   - Removed app: namespace attributes
   - Used standard Android attributes only

3. **bg_circle_white.xml** (NEW)
   - Simple oval shape drawable
   - White solid color
   - Replaces MaterialCardView circular background

## How to Test

### Method 1: Using Android Studio
1. Open project in Android Studio
2. Click "Sync Project with Gradle Files"
3. Wait for sync to complete
4. Click "Run" button (green play icon)
5. Select device/emulator
6. App should launch with splash screen

### Method 2: Command Line Build
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Method 3: Direct APK Install
```bash
# Build APK
./gradlew assembleDebug

# Find APK at:
# app/build/outputs/apk/debug/app-debug.apk

# Install manually
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

### Issue: "JAVA_HOME is set to an invalid directory"
**Solution:**
1. Find your Java installation:
   ```bash
   where java
   ```
2. Set JAVA_HOME (remove \bin from path):
   ```bash
   # If Java is at: C:\Program Files\Java\jdk-23\bin\java.exe
   # Set JAVA_HOME to: C:\Program Files\Java\jdk-23
   
   setx JAVA_HOME "C:\Program Files\Java\jdk-23"
   ```
3. Restart terminal and try again

### Issue: App still crashes on startup
**Check these:**
1. Verify all drawable resources exist:
   - `bg_gradient_primary.xml`
   - `bg_circle_white.xml`
   - `ic_launcher_round` in mipmap folders

2. Check AndroidManifest.xml:
   - SplashActivity has `android:exported="true"`
   - SplashActivity has MAIN/LAUNCHER intent filter
   - LoginActivity exists and is registered

3. View logcat for error details:
   ```bash
   adb logcat | grep -i "error\|exception"
   ```

### Issue: Animations not smooth
**Solutions:**
- Enable "Force GPU rendering" in Developer Options
- Disable "Window animation scale" for testing
- Reduce animation durations in SplashActivity

### Issue: White screen instead of gradient
**Check:**
- `bg_gradient_primary.xml` exists in drawable folder
- File has proper gradient definition
- No syntax errors in XML

## Quick Fix: Skip Splash Screen

If splash continues to cause issues, you can temporarily skip it:

1. Open `AndroidManifest.xml`
2. Find the SplashActivity `<intent-filter>` block
3. Move it to LoginActivity instead:

```xml
<!-- Login Activity (Temporary Launcher) -->
<activity
    android:name=".view.LoginActivity"
    android:exported="true"
    android:theme="@style/Theme.AppCompat.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<!-- Splash Screen (Disabled) -->
<activity
    android:name=".view.SplashActivity"
    android:exported="false"
    android:theme="@style/Theme.AppCompat.NoActionBar"/>
```

This makes LoginActivity the launcher instead of SplashActivity.

## Expected Behavior

When working correctly:
1. App launches showing gradient background
2. White circular logo container bounces in (1 second)
3. Logo icon rotates into view (0.8 seconds)
4. "AIMS" text slides up (0.8 seconds)
5. Subtitle fades in (0.6 seconds)
6. Progress indicator appears (0.5 seconds)
7. After 2.5 seconds total, fades to LoginActivity

## Contact

If issues persist, check:
- Android Studio version (should be latest)
- Gradle version compatibility
- Android SDK installed correctly
- Device/emulator API level (minimum API 24)
