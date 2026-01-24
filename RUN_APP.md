# 🚀 How to Run AIMS App

## Fastest Way (Android Studio)

1. **Open Android Studio**
2. **Open this project folder**
3. **Wait for "Gradle sync" to finish** (bottom right corner)
4. **Click the green play button** ▶ at the top
5. **Select your device** (phone or emulator)
6. **Done!** App will install and launch

---

## Using Command Line (Windows)

### Quick Method
Double-click: `build_and_run.bat`

### Manual Method
Open PowerShell or CMD in this folder:

```bash
# Build the app
gradlew assembleDebug

# Install on connected device
gradlew installDebug
```

---

## Common Issues & Solutions

### ❌ "JAVA_HOME is set to an invalid directory"

**Fix:**
1. Find Java location:
   ```bash
   where java
   ```
   Example output: `C:\Program Files\Java\jdk-23\bin\java.exe`

2. Set JAVA_HOME (remove `\bin` from path):
   ```bash
   setx JAVA_HOME "C:\Program Files\Java\jdk-23"
   ```

3. **Close and reopen terminal**, then try again

---

### ❌ "No devices found"

**For Phone:**
1. Connect phone via USB
2. Enable "Developer Options" on phone
3. Enable "USB Debugging"
4. Accept the prompt on phone
5. Run: `adb devices` to verify

**For Emulator:**
1. Android Studio → Tools → Device Manager
2. Click "Create Device"
3. Select a device (e.g., Pixel 5)
4. Download system image if needed
5. Click "Finish" and start emulator

---

### ❌ "App keeps stopping"

**Solution 1: Clear and reinstall**
```bash
adb uninstall com.narith.aims
gradlew installDebug
```

**Solution 2: Check device requirements**
- Android 7.0 or higher
- At least 100MB free space

**Solution 3: View error logs**
```bash
adb logcat | findstr "AndroidRuntime"
```

---

### ❌ Gradle sync fails

1. Check internet connection
2. In Android Studio: File → Invalidate Caches → Restart
3. Try: `gradlew clean build`

---

## Test the App

Once running, try these test accounts:

**Admin (Full Access):**
- Username: `admin`
- Password: `1234`

**User (Sales Only):**
- Username: `user`
- Password: `1234`

---

## What You Should See

1. **Splash Screen** (2.5 seconds)
   - Purple gradient background
   - Animated logo
   - "AIMS" text
   - Loading indicator

2. **Login Screen**
   - Username and password fields
   - Login button

3. **Main Dashboard**
   - Product list
   - Menu buttons
   - User info at top

---

## Still Not Working?

Check these detailed guides:
- `QUICK_START.md` - Complete setup guide
- `SPLASH_FIX_GUIDE.md` - Troubleshooting
- `SPLASH_SCREEN_FIXED.md` - Technical details

Or try the temporary workaround in `SPLASH_FIX_GUIDE.md` to skip the splash screen.

---

## Need Help?

Make sure you have:
- ✅ Android Studio installed
- ✅ Java JDK installed
- ✅ Android SDK installed
- ✅ Device connected OR emulator running
- ✅ USB debugging enabled (for phone)
- ✅ Internet connection (for first build)

Good luck! 🎉
