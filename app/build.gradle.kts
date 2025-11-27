plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.daggerHiltAndroid)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

hilt {
    enableAggregatingTask = false
}

android {
    namespace = "dev.leotoloza.avengersapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.leotoloza.avengersapp"
        minSdk = 30
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
//    Standard libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.runtime.saveable)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    Viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
//    Navigation
    implementation(libs.androidx.navigation.compose)
//    Coil para carga de imagenes
    implementation(libs.coil.compose)
//    Dagger Hilt + ksp para inyeccion de dependencias
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.google.dagger.hilt.android.compiler)
//    Retrofit2 con Moshi Converter
    implementation (libs.moshi.kotlin)
    implementation (libs.converter.moshi)
//    OkHttp con Logging Interceptor
    implementation(platform(libs.okhttp.bom))
    implementation(libs.logging.interceptor)
//    Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-perf")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-inappmessaging-display")
    implementation("com.firebaseui:firebase-ui-auth:9.0.0")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
}