# 🚀 Test the Fix NOW

## Fastest Way to Test

### Option 1: Double-click this file
```
rebuild_and_test.bat
```

### Option 2: Copy-paste this command
```bash
adb uninstall com.narith.aims && gradlew clean assembleDebug installDebug && adb shell am start -n com.narith.aims/.view.SplashActivity
```

### Option 3: Use Android Studio
1. Click "Build" → "Clean Project"
2. Click "Build" → "Rebuild Project"  
3. Click "Run" button (▶)
4. Select device
5. Watch it launch!

---

## What You Should See

### ✅ SUCCESS (No Crash)
```
Splash Screen appears
  ↓
Logo animates in (smooth)
  ↓
Text fades in
  ↓
Progress indicator shows
  ↓
After 2 seconds...
  ↓
Smooth fade to Login Screen
  ↓
Login screen loads
  ↓
Ready to use!
```

### ❌ FAILURE (Still Crashes)
```
Splash Screen appears
  ↓
Animations play
  ↓
Screen goes black
  ↓
"AIMS keeps stopping" message
```

**If this happens:**
1. Run: `adb logcat | findstr "AndroidRuntime"`
2. Copy the error message
3. Check `CRASH_FIX_APPLIED.md` for solutions

---

## Quick Checks

### Is device connected?
```bash
adb devices
```
Should show your device listed.

### Is app installed?
```bash
adb shell pm list packages | findstr aims
```
Should show: `package:com.narith.aims`

### Can we launch it?
```bash
adb shell am start -n com.narith.aims/.view.SplashActivity
```
Should launch the app.

---

## Monitor for Crashes

Open a second terminal and run:
```bash
adb logcat -c && adb logcat | findstr "AndroidRuntime"
```

Leave this running while you test. If it crashes, you'll see the error immediately.

---

## Test Checklist

- [ ] Uninstalled old version
- [ ] Cleaned project
- [ ] Built new APK
- [ ] Installed successfully
- [ ] Launched app
- [ ] Splash screen appeared
- [ ] Animations played smoothly
- [ ] Transitioned to login (no crash!)
- [ ] Login screen loaded
- [ ] Can type in username/password

---

## If Everything Works

🎉 **Congratulations!** The crash is fixed!

Now test the login:
- Username: `admin`
- Password: `1234`

Should take you to the main dashboard.

---

## If Still Crashing

### Quick Fix: Skip Splash Screen

1. Open `AndroidManifest.xml`
2. Find this section:
```xml
<activity
    android:name=".view.SplashActivity"
    android:exported="true"
    ...>
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

3. Move the `<intent-filter>` to LoginActivity instead
4. Rebuild and test

This makes the app start directly at login, bypassing the splash screen.

---

## Need More Help?

Check these files:
- `FIX_SUMMARY.md` - Complete fix details
- `CRASH_FIX_APPLIED.md` - Troubleshooting guide
- `QUICK_START.md` - Setup instructions

---

## Ready? Let's Test!

Run this now:
```bash
rebuild_and_test.bat
```

Watch your device and see the magic happen! ✨
