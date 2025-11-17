plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
}

java {
    JavaVersion.toVersion(libs.versions.jvmTarget.get()).let {
        sourceCompatibility = it
        targetCompatibility = it
    }
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
}
