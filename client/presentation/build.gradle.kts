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
    implementation ("com.kakao.sdk:v2-all:2.17.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation ("com.kakao.sdk:v2-user:2.17.0") // 카카오 로그인
    implementation ("com.kakao.sdk:v2-talk:2.17.0") // 친구, 메시지(카카오톡)
    implementation ("com.kakao.sdk:v2-story:2.17.0") // 카카오스토리
    implementation ("com.kakao.sdk:v2-share:2.17.0") // 메시지(카카오톡 공유)
    implementation ("com.kakao.sdk:v2-friend:2.17.0") // 카카오톡 소셜 피커, 리소스 번들 파일 포함
    implementation ("com.kakao.sdk:v2-navi:2.17.0") // 카카오내비
    implementation ("com.kakao.sdk:v2-cert:2.17.0") // 카카오 인증서비스
}