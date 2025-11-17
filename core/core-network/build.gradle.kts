import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.midas.core.network"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "BASE_URL", "\"https://api.coingecko.com/api/v3/\"")
        buildConfigField("String", "API_KEY", "\"CG-vWEECX73NuWQVfcevWrg5HYw\"")
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
        JavaVersion.toVersion(libs.versions.jvmTarget.get()).let {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.bundles.ktor)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
