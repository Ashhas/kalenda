import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}
val oauthClientId: String = localProperties.getProperty("GOOGLE_OAUTH_CLIENT_ID", "")
require(oauthClientId.isNotBlank()) {
    "GOOGLE_OAUTH_CLIENT_ID is missing. Add it to local.properties before building."
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions { jvmTarget = "11" }
        }
    }
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ConfiguratorApp"
            isStatic = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(project(":widget-android"))
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.glance.material3)
            implementation(libs.androidx.work.runtime.ktx)
            implementation("androidx.browser:browser:1.8.0")
        }
    }
}

android {
    namespace = "nl.ashhasstudio.kalenda.configurator"
    compileSdk = 35
    defaultConfig {
        applicationId = "nl.ashhasstudio.kalenda"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"$oauthClientId\"")
        val reverseClientId = oauthClientId.split(".").reversed().joinToString(".")
        manifestPlaceholders["oauthRedirectScheme"] = reverseClientId
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
