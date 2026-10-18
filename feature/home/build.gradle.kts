plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id(Deps.Plugins.KOTLIN_SERIALIZATION)
}

android {
    namespace = "app.moviedb.home"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 30

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

    @Suppress("AvoidDuplicateDependencies")
    implementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    implementation(Deps.ANDROIDX_ACTIVITY_COMPOSE)
    implementation(Deps.ANDROIDX_COMPOSE_UI)
    implementation(Deps.ANDROIDX_COMPOSE_UI_GRAPHICS)
    implementation(Deps.ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Deps.ANDROIDX_COMPOSE_MATERIAL3)
    implementation(Deps.ANDROIDX_COMPOSE_ICON)

    implementation(Deps.ANDROIDX_CORE_KTX)
    implementation(Deps.ANDROIDX_LIFECYCLE_RUNTIME_KTX)

    // test and development
    testImplementation(Deps.JUNIT)
    androidTestImplementation(Deps.ANDROIDX_JUNIT)
    androidTestImplementation(Deps.ANDROIDX_ESPRESSO_CORE)
    @Suppress("AvoidDuplicateDependencies")
    androidTestImplementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    androidTestImplementation(Deps.ANDROIDX_COMPOSE_UI_TEST_JUNIT4)
    debugImplementation(Deps.ANDROIDX_COMPOSE_UI_TOOLING)
    debugImplementation(Deps.ANDROIDX_COMPOSE_UI_TEST_MANIFEST)
}