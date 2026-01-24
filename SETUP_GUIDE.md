# Complete Firebase Setup Guide

## Part 1: Firebase Console Setup (You Must Do This Manually)

### Step 1: Create Firebase Project
1. Open your web browser
2. Go to: https://console.firebase.google.com/
3. Click "Add project" or select existing project
4. Follow the setup wizard

### Step 2: Add Android App to Firebase
1. In Firebase Console, click the Android icon (or "Add app")
2. Enter these details:
   - **Package name**: `com.narith.aims` (IMPORTANT: Must match exactly)
   - **App nickname**: AIMS (optional)
   - **Debug signing certificate SHA-1**: (optional for now)
3. Click "Register app"

### Step 3: Download Configuration File
1. Click "Download google-services.json"
2. Save the file to your computer
3. **IMPORTANT**: Move this file to your project's `app/` folder
   - The file should be at: `YourProject/app/google-services.json`
   - NOT in `app/src/` folder
   - It should be next to `build.gradle.kts`

### Step 4: Enable Firebase Services
In Firebase Console:
1. Go to **Authentication** → Sign-in method
2. Enable "Email/Password" provider
3. Go to **Firestore Database**
4. Click "Create database"
5. Choose "Start in test mode" (for development)
6. Select a location and click "Enable"

---

## Part 2: After You Add google-services.json

Once you've placed `google-services.json` in the `app/` folder, run these commands:

### Windows (CMD):
```cmd
cd path\to\your\project
gradlew clean
gradlew build
```

### Windows (PowerShell):
```powershell
cd path\to\your\project
.\gradlew clean
.\gradlew build
```

### If gradlew doesn't exist:
```cmd
gradle wrapper
.\gradlew clean build
```

---

## Part 3: Verify Setup

After building, check:
1. ✅ `app/google-services.json` exists
2. ✅ Build completes without errors
3. ✅ App runs without SecurityException

---

## Troubleshooting

### Error: "google-services.json is missing"
- Make sure the file is in `app/` folder, not `app/src/`
- File name must be exactly `google-services.json`

### Error: "Package name mismatch"
- Package name in google-services.json must be `com.narith.aims`
- If wrong, download a new file from Firebase Console

### Error: "Plugin not found"
- Make sure you uncommented the plugin line in build.gradle.kts
- Sync Gradle files

---

## What I've Already Done For You
✅ Fixed AndroidManifest.xml (added missing activities)
✅ Added CAMERA permission
✅ Added Google Play Services metadata
✅ Ready to uncomment google-services plugin (waiting for your file)
