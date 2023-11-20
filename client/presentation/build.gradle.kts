import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.lang)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.everyone.movemove_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.everyone.movemove_android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String","KAKAO_NATIVE_APP_KEY", getApiKey("KAKAO_NATIVE_APP_KEY"))
        buildConfigField("String","GOOGLE_CLIENT_ID", getApiKey("GOOGLE_CLIENT_ID"))
        resValue("string","KAKAO_OAUTH_HOST", getApiKey("KAKAO_OAUTH_HOST"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

fun getApiKey(propertyKey: String): String{
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.bundles.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.common)

    // ksp
    ksp(libs.ksp.hilt)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.junit.espresso)
    debugImplementation(libs.compose.ui.test)
    debugImplementation(libs.compose.ui.tooling.debug)
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))

    //kakao
    implementation(libs.kakao.all)
    implementation(libs.kakao.user)
    implementation(libs.kakao.talk)
    implementation(libs.kakao.story)
    implementation(libs.kakao.share)
    implementation(libs.kakao.friend)
    implementation(libs.kakao.navi)
    implementation(libs.kakao.cert)

    //google
    implementation (libs.google.services)
    implementation (libs.firebase.auth)
    implementation (libs.firebase.bom)
    implementation (libs.google.auth)
}