plugins {
    kotlin("jvm")
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
    implementation(libs.ktor.client.core)
}
