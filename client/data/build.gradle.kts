plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.lang)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.parcelize)
}

android {
    namespace = "com.everyone.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.bundles.common)

    // ksp
    ksp(libs.ksp.hilt)

    // ktor
    implementation(libs.bundles.ktor)

    // datastore
    implementation(libs.bundles.datastore)

    // test
    testImplementation(libs.kotest)
    testImplementation(libs.mockK)
}