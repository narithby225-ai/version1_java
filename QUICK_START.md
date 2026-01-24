# Quick Start Guide - AIMS App

## Option 1: Using Android Studio (RECOMMENDED)

This is the easiest and most reliable method:

1. **Open Project**
   - Launch Android Studio
   - Click "Open" and select this project folder
   - Wait for Gradle sync to complete (bottom right corner)

2. **Sync Project**
   - Click "File" → "Sync Project with Gradle Files"
   - Wait for sync to finish (watch bottom status bar)

3. **Run App**
   - Connect Android device via USB (enable USB debugging)
   - OR start an Android emulator
   - Click the green "Run" button (▶) at the top
   - Select your device/emulator
   - Wait for app to install and launch

## Option 2: Using Command Line

### Windows (PowerShell or CMD)

```bash
# Run the automated script
build_and_run.bat
```

OR manually:

```bash
# Clean and build
gradlew clean assembleDebug

# Install on device
gradlew installDebug
```

### If You Get JAVA_HOME Error

1. Find your Java installation:
   ```bash
   where java
   ```

2. Set JAVA_HOME (remove \bin from the path):
   ```bash
   # Example: If java is at C:\Program Files\Java\jdk-23\bin\java.exe
   setx JAVA_HOME "C:\Program Files\Java\jdk-23"
   ```

3. Close and reopen terminal, then try again

## Option 3: Build APK Only

If you just want to build the APK file:

```bash
gradlew assembleDebug
```

The APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

Install manually:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

### "App keeps stopping" on launch

1. **Check logcat for errors:**
   ```bash
   adb logcat | findstr "AndroidRuntime"
   ```

2. **Verify device requirements:**
   - Android 7.0 (API 24) or higher
   - At least 100MB free space

3. **Clear app data and reinstall:**
   ```bash
   adb uninstall com.narith.aims
   gradlew installDebug
   ```

### Gradle sync fails

1. **Check internet connection** (Gradle downloads dependencies)
2. **Invalidate caches:**
   - Android Studio → File → Invalidate Caches → Invalidate and Restart
3. **Update Gradle wrapper:**
   ```bash
   gradlew wrapper --gradle-version=8.7
   ```

### No devices found

1. **For physical device:**
   - Enable Developer Options
   - Enable USB Debugging
   - Accept RSA key prompt on device
   - Run: `adb devices` to verify

2. **For emulator:**
   - Android Studio → Tools → Device Manager
   - Create new virtual device
   - Start emulator

### Build is very slow

1. **Enable Gradle daemon:**
   - Add to `gradle.properties`:
     ```
     org.gradle.daemon=true
     org.gradle.parallel=true
     org.gradle.caching=true
     ```

2. **Increase heap size:**
   - Add to `gradle.properties`:
     ```
     org.gradle.jvmargs=-Xmx4096m
     ```

## Test Accounts

Once the app launches successfully:

- **Admin Account:**
  - Username: `admin`
  - Password: `1234`
  - Access: Full system access

- **User Account:**
  - Username: `user`
  - Password: `1234`
  - Access: Sales only

## Expected App Flow

1. **Splash Screen** (2.5 seconds)
   - Animated logo
   - App name and subtitle
   - Loading indicator

2. **Login Screen**
   - Enter username and password
   - Click "LOGIN" button

3. **Main Dashboard**
   - View products
   - Access all features
   - Logout option in menu

## Features to Test

✅ Login/Logout
✅ Product management (Add/Edit/Delete)
✅ Stock management (Import/Export)
✅ Customer test mode (Sales)
✅ Discount system
✅ Reports and analytics
✅ Barcode scanning

## Need Help?

If you're still having issues:

1. Check `SPLASH_FIX_GUIDE.md` for detailed troubleshooting
2. Review error messages in Android Studio's Logcat
3. Ensure all dependencies are downloaded
4. Try cleaning and rebuilding: `gradlew clean build`

## Success Indicators

You'll know it's working when:
- ✅ Gradle sync completes without errors
- ✅ Build succeeds (no red errors in console)
- ✅ App installs on device
- ✅ Splash screen appears with animations
- ✅ Login screen loads after splash
- ✅ Can login with test credentials

Good luck! 🚀
