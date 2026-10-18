import java.io.FileInputStream
import java.util.Properties

plugins {
    id(Deps.Plugins.ANDROID_LIBRARY)
    id(Deps.Plugins.KOTLIN_SERIALIZATION)
}

android {
    namespace = "app.moviedb.core"
    compileSdk {
        version = release(Deps.COMPILE_SDK)
    }

    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            load(FileInputStream(file))
        }
    }

    defaultConfig {
        minSdk = Deps.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "ACCESS_TOKEN", "\"${localProperties.getProperty("access_token", "")}\"")
        buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("api_key", "")}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(Deps.KOTLINX_SERIALIZATION_JSON)

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(Deps.ANDROIDX_CORE_KTX)
    implementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    implementation(Deps.ANDROIDX_COMPOSE_MATERIAL3)
    testImplementation(Deps.JUNIT)
    androidTestImplementation(Deps.ANDROIDX_ESPRESSO_CORE)
    androidTestImplementation(Deps.ANDROIDX_JUNIT)
}