# Firebase Setup Instructions - CRITICAL

## The Problem
Your app is crashing with `SecurityException: Unknown calling package name 'com.google.android.gms'` because Firebase/Google Play Services cannot verify your app without the `google-services.json` configuration file.

## Solution: Get google-services.json File

### Step 1: Go to Firebase Console
1. Visit https://console.firebase.google.com/
2. Sign in with your Google account

### Step 2: Create or Select Project
- If you have an existing Firebase project for this app, select it
- If not, click "Add project" and follow the wizard

### Step 3: Add Android App
1. Click the Android icon or "Add app" button
2. Enter your package name: `com.narith.aims`
3. (Optional) Add app nickname: "AIMS"
4. (Optional) Add SHA-1 certificate (for release builds)
5. Click "Register app"

### Step 4: Download google-services.json
1. Download the `google-services.json` file
2. Place it in: `app/google-services.json` (in your project's app folder)

### Step 5: Enable Firebase Services
In Firebase Console, enable these services:
- **Authentication** → Enable Email/Password sign-in method
- **Firestore Database** → Create database (start in test mode for development)

### Step 6: Uncomment Google Services Plugin
In `app/build.gradle.kts`, uncomment this line:
```kotlin
id("com.google.gms.google-services")
```

### Step 7: Rebuild Project
```bash
./gradlew clean build
```

## Alternative: Use Local Database (No Firebase)

If you don't want to use Firebase, you need to:

1. Remove Firebase dependencies from `build.gradle.kts`
2. Modify repositories to use only SQLite (DatabaseHelper)
3. Remove Firebase authentication and use local auth
4. This requires significant code changes

## What I've Fixed Already
✅ Added missing activities to AndroidManifest.xml
✅ Added CAMERA permission for ScannerActivity
✅ Added Google Play Services metadata to manifest

## What You Need To Do
❌ Get `google-services.json` from Firebase Console
❌ Place it in `app/` folder
❌ Uncomment the google-services plugin
❌ Rebuild the app
