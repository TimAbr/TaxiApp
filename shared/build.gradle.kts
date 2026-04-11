import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildConfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    iosArm64()
    iosSimulatorArm64()
    
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)
            implementation(libs.koin.android)
        }

        jvmMain.dependencies {
            implementation(libs.google.api.client)
            implementation(libs.google.oauth.client.jetty)
            implementation(libs.google.auth.library.oauth2.http)
        }
    }
}

val props = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

val googleWebClientId = props.getProperty("google.web.client.id.android") ?: ""
val googleDesktopId = props.getProperty("google.desktop.client.id") ?: ""
val googleDesktopSecret = props.getProperty("google.desktop.client.secret") ?: ""

android {
    namespace = "org.example.project.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        val webClientId = props.getProperty("google.web.client.id.android") ?: ""
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$webClientId\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

buildConfig {
    sourceSets.named("jvmMain") {
        className.set("DesktopConfig")

        val desktopId = props.getProperty("google.desktop.client.id") ?: ""
        val desktopSecret = props.getProperty("google.desktop.client.secret") ?: ""

        buildConfigField("String", "GOOGLE_DESKTOP_ID", "\"$desktopId\"")
        buildConfigField("String", "GOOGLE_DESKTOP_SECRET", "\"$desktopSecret\"")
    }
}
