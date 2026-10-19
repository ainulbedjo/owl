plugins {
    id(Deps.Plugins.ANDROID_LIBRARY)
    id(Deps.Plugins.KOTLIN_COMPOSE)
    id(Deps.Plugins.KOTLIN_SERIALIZATION)
    id(Deps.Plugins.KSP)
}

android {
    namespace = "app.moviedb.favorites"
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

dependencies {
    implementation(project(":core"))
    implementation(project(":designsystem"))
    implementation(Deps.KOTLINX_SERIALIZATION_JSON)
    implementation(Deps.ANDROIDX_NAVIGATION_COMPOSE)

    // DI and ViewModel
    implementation(Deps.HILT_ANDROID)
    ksp(Deps.HILT_COMPILER)
    implementation(Deps.HILT_NAVIGATION_COMPOSE)
    implementation(Deps.ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE)

    @Suppress("AvoidDuplicateDependencies")
    implementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    implementation(Deps.ANDROIDX_COMPOSE_UI)
    implementation(Deps.ANDROIDX_COMPOSE_UI_GRAPHICS)
    implementation(Deps.ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Deps.ANDROIDX_COMPOSE_MATERIAL3)
    implementation(Deps.ANDROIDX_COMPOSE_ICON)

    // test and development
    testImplementation(Deps.JUNIT)
    testImplementation(Deps.COROUTINES_TEST)
    androidTestImplementation(Deps.ANDROIDX_JUNIT)
    androidTestImplementation(Deps.ANDROIDX_ESPRESSO_CORE)
    @Suppress("AvoidDuplicateDependencies")
    androidTestImplementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    androidTestImplementation(Deps.ANDROIDX_COMPOSE_UI_TEST_JUNIT4)
    debugImplementation(Deps.ANDROIDX_COMPOSE_UI_TOOLING)
    debugImplementation(Deps.ANDROIDX_COMPOSE_UI_TEST_MANIFEST)
}
