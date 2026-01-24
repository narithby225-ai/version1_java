plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.narith.aims"

    // Fix: Updated to 36 to satisfy the new androidx requirements
    compileSdk = 36

    defaultConfig {
        applicationId = "com.narith.aims"
        minSdk = 24

        // targetSdk can stay at 35 or move to 36
        // Moving to 36 ensures you are ready for 2026 Play Store requirements
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64"))
        }
    }

    packaging {
        jniLibs {
            // Keep this false to fix the 16 KB alignment issue from earlier
            useLegacyPackaging = false
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // ML Kit & CameraX
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Material Design (for Bottom Navigation & Cards)
    implementation("com.google.android.material:material:1.11.0")

    // Circular Image View (for the round product images)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}