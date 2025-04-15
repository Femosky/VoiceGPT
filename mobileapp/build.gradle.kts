plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.group6finalgroupproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.group6finalgroupproject"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ChaGPT API Key
        val chatGPTApiKey: String = project.findProperty("CHATGPT_API_KEY") as? String ?: "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"
        buildConfigField("String", "CHATGPT_API_KEY", "\"$chatGPTApiKey\"" )
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.gson)
}