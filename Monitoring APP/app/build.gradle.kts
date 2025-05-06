plugins {
    alias(libs.plugins.android.application)
    // This likely translates to `com.android.application`
}

android {
    namespace = "com.example.mobilemonitoringapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mobilemonitoringapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // This ensures you're using Java 11 for compilation. Perfectly fine.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)        // likely androidx.appcompat:appcompat:X.X.X
    implementation(libs.material)        // likely com.google.android.material:material:X.X.X
    implementation(libs.activity)        // likely androidx.activity:activity:X.X.X
    implementation(libs.constraintlayout)// likely androidx.constraintlayout:constraintlayout:X.X.X

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation(libs.okhttp)
    implementation(libs.gson)





    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}