import java.io.FileInputStream
import java.util.Properties

plugins {
    id(Deps.Plugins.ANDROID_LIBRARY)
    id(Deps.Plugins.KOTLIN_SERIALIZATION)
    id(Deps.Plugins.KSP)
    id(Deps.Plugins.HILT)
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
        buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("base_url", "")}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

// No Compose here: this module is data and logic only. UI lives in :designsystem and the features.
dependencies {
    implementation(Deps.RETROFIT)
    implementation(Deps.RETROFIT_SERIALIZATION_CONVERTER)
    implementation(Deps.KOTLINX_SERIALIZATION_JSON)

    // DI
    implementation(Deps.HILT_ANDROID)
    ksp(Deps.HILT_COMPILER)

    // Http & inspector
    debugImplementation(Deps.CHUCKER)
    releaseImplementation(Deps.CHUCKER_RELEASE)
    implementation(Deps.OKHTTP)

    // test and development
    testImplementation(Deps.JUNIT)
    testImplementation(Deps.COROUTINES_TEST)
    androidTestImplementation(Deps.ANDROIDX_JUNIT)
    androidTestImplementation(Deps.ANDROIDX_ESPRESSO_CORE)
}
