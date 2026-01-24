# System Issues Found in AIMS App

## Critical Issues

### 1. Missing Activities in AndroidManifest.xml
The following activities exist in code but are NOT declared in the manifest:
- `ScannerActivity` - Used for barcode scanning
- `CategoryActivity` - Category management
- `CartActivity` - Shopping cart functionality
- `PaymentActivity` - Payment processing
- `ReportActivity` - Reports functionality
- `AddProductActivity` - Adding new products

**Impact**: These activities will crash the app when launched with `ActivityNotFoundException`

**Fix Required**: Add these activity declarations to AndroidManifest.xml

---

### 2. Missing Camera Permission
`ScannerActivity` uses camera functionality but the manifest is missing:
```xml
<uses-permission android:name="android.permission.CAMERA" />
```

**Impact**: Camera will not work, app may crash when trying to access camera

**Fix Required**: Add CAMERA permission to AndroidManifest.xml

---

## Medium Priority Issues

### 3. Firebase Configuration
The build.gradle has Firebase dependencies but Google Services plugin is commented out:
```kotlin
// id("com.google.gms.google-services") // Temporarily disabled
```

**Impact**: Firebase authentication and Firestore may not work properly without proper configuration

**Fix Required**: 
- Add `google-services.json` file
- Uncomment the plugin line

---

### 4. Duplicate MainActivity
There are TWO MainActivity files:
- `app/src/main/java/com/narith/aims/MainActivity.java`
- `app/src/main/java/com/narith/aims/view/MainActivity.java`

**Impact**: Confusion, potential build issues, wrong activity may be launched

**Fix Required**: Remove the duplicate and keep only one (likely the one in view package)

---

## Low Priority Issues

### 5. Empty Activity Classes
These activities are empty placeholders:
- `ReportActivity` - No implementation
- `PaymentActivity` - No implementation

**Impact**: These screens will be blank/non-functional

---

### 6. Error Handling
Some error handling uses `printStackTrace()` instead of proper logging:
- `ScannerActivity.java` line 43, 86
- `BaseApplication.java` line 14

**Impact**: Harder to debug in production

---

## Recommendations

1. **Immediate**: Fix the manifest issues (missing activities and camera permission)
2. **High Priority**: Resolve Firebase configuration
3. **Medium Priority**: Remove duplicate MainActivity
4. **Low Priority**: Implement empty activities and improve error handling
