plugins {
    id(Deps.Plugins.ANDROID_LIBRARY)
    id(Deps.Plugins.KOTLIN_COMPOSE)
}

android {
    namespace = "app.moviedb.designsystem"
    compileSdk {
        version = release(Deps.COMPILE_SDK)
    }

    defaultConfig {
        minSdk = Deps.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Compose UI only. This module must not depend on :core, so it never sees Movie or SectionState.
dependencies {
    @Suppress("AvoidDuplicateDependencies")
    implementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    implementation(Deps.ANDROIDX_COMPOSE_UI)
    implementation(Deps.ANDROIDX_COMPOSE_UI_GRAPHICS)
    implementation(Deps.ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Deps.ANDROIDX_COMPOSE_MATERIAL3)
    implementation(Deps.ANDROIDX_COMPOSE_ICON)

    // Image loading stays in this module: features pass a URL and never touch Coil.
    implementation(Deps.COIL_COMPOSE)
    implementation(Deps.COIL_NETWORK_OKHTTP)

    // test and development
    testImplementation(Deps.JUNIT)
    @Suppress("AvoidDuplicateDependencies")
    androidTestImplementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    debugImplementation(Deps.ANDROIDX_COMPOSE_UI_TOOLING)
}
