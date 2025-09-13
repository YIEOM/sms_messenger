plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val KEY_ALIAS: String by project
val KEY_PASSWORD: String by project
val STORE_FILE: String by project
val STORE_PASSWORD: String by project

android {
    signingConfigs {
        create("release") {
            keyAlias = KEY_ALIAS
            keyPassword = KEY_PASSWORD
            storeFile = file(STORE_FILE)
            storePassword = STORE_PASSWORD
        }
    }

    namespace = "com.yieom.smsmessenger"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yieom.smsmessenger"
        minSdk = 31
        targetSdk = 35
        versionCode = 4
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packagingOptions {
        resources {
            pickFirsts += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.android.database.sqlcipher)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.sqlite.framework)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.timber)

    // Google Sign-In (Authentication)
    implementation(libs.google.gms.auth)

    // Google API 관련 라이브러리 묶음 (Bundle)
    // 한 줄로 4개의 라이브러리를 추가합니다.
    implementation(libs.bundles.google.api)

    // Coroutines for background tasks
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.datastore.preferences)
}
