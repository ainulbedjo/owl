
plugins {
    id(Deps.Plugins.ANDROID_APPLICATION)
    id(Deps.Plugins.KOTLIN_COMPOSE)
}

android {
    namespace = Deps.NAMESPACE
    compileSdk = Deps.COMPILE_SDK

    defaultConfig {
        applicationId = Deps.NAMESPACE
        minSdk = Deps.MIN_SDK
        targetSdk = Deps.TARGET_SDK // minimum sdk version for compose android
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            isProfileable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            isProfileable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {

    implementation(project(":core"))
    implementation(project(":designsystem"))
    implementation(project(":feature:home"))
    implementation(project(":feature:detail"))

    implementation(Deps.ANDROIDX_CORE_KTX)
    implementation(Deps.ANDROIDX_LIFECYCLE_RUNTIME_KTX)
    implementation(Deps.ANDROIDX_ACTIVITY_COMPOSE)
    implementation(Deps.ANDROIDX_NAVIGATION_COMPOSE)
    // one is implementation,
    // the other is the test implementation
    @Suppress("AvoidDuplicateDependencies")
    implementation(platform(Deps.ANDROIDX_COMPOSE_BOM))
    implementation(Deps.ANDROIDX_COMPOSE_UI)
    implementation(Deps.ANDROIDX_COMPOSE_UI_GRAPHICS)
    implementation(Deps.ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Deps.ANDROIDX_COMPOSE_MATERIAL3)
    implementation(Deps.ANDROIDX_COMPOSE_ICON)

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