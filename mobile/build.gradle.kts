plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mobile"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}