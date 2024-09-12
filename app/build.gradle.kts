plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")

}

android {
    namespace = "com.openclassrooms.p8_vitesse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.openclassrooms.p8_vitesse"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)


    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    kapt (libs.androidx.room.compiler)
    implementation (libs.androidx.room.runtime)

    implementation(libs.firebase.functions.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // UI
    implementation (libs.androidx.constraintlayout)
    implementation (libs.androidx.appcompat)
    implementation (libs.androidx.core.ktx)
    implementation (libs.material)
    implementation (libs.androidx.viewpager)
    implementation (libs.androidx.fragment)


    // DI Koin
    implementation(libs.koin.android)
    implementation(libs.koin.core)

    // Glide
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    // Retrofit for Network Requests
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.moshi)

    // Moshi JSON Library
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

}