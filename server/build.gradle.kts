plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "${libs.versions.kotlin.get()}"
    application
}

group = "org.example.project"
version = "1.0.0"
application {
    mainClass.set("org.example.project.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serializationJson)
    implementation(libs.ktor.serverAuth)
    implementation(libs.ktor.serverAuthJwt)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.kotlinx.datetime)
    implementation(libs.h2)
    implementation(libs.javaJwt)
    implementation(libs.googleApiClient)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}