// need to suppress, cuz the props will be generated into a .class file
@Suppress("unused")
object Deps {
    const val APP_NAME = "MovieDB"

    const val NAMESPACE = "app.moviedb"
    const val MIN_SDK = 30
    const val TARGET_SDK = 37
    const val COMPILE_SDK = 37

    object Versions {
        const val AGP = "9.1.0"
        const val KOTLIN = "2.2.10"
        const val CORE_KTX = "1.18.0"
        const val JUNIT = "4.13.2"
        const val JUNIT_EXT = "1.1.5"
        const val ESPRESSO_CORE = "3.5.1"
        const val LIFECYCLE_RUNTIME_KTX = "2.11.0"
        const val ACTIVITY_COMPOSE = "1.13.0"
        const val COMPOSE_BOM = "2026.09.00" // to accommodate M3 expressive
        const val MATERIAL3 = "1.5.0-beta01"
        const val NAVIGATION_COMPOSE = "2.9.8"
        const val KOTLINX_SERIALIZATION = "1.11.0"
        const val COROUTINES = "1.10.2"

        // LIBRARIES
        const val CHUCKER = "4.2.0"
        const val RETROFIT = "2.11.0"
        const val LEAK_CANARY = "2.14"
        const val HILT = "2.60.1"
        const val HILT_NAVIGATION_COMPOSE = "1.3.0"
        const val KSP = "2.3.12"
        const val OKHTTP = "5.5.0"
    }

    object Plugins {
        const val ANDROID_APPLICATION = "com.android.application"
        const val KOTLIN_COMPOSE = "org.jetbrains.kotlin.plugin.compose"
        const val KOTLIN_SERIALIZATION = "org.jetbrains.kotlin.plugin.serialization"
        const val ANDROID_LIBRARY = "com.android.library"
        const val HILT = "com.google.dagger.hilt.android"
        const val KSP = "com.google.devtools.ksp"
    }

    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val ANDROIDX_LIFECYCLE_RUNTIME_KTX =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_RUNTIME_KTX}"
    const val COROUTINES_TEST =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"

    // COMPOSE
    const val ANDROIDX_ACTIVITY_COMPOSE =
        "androidx.activity:activity-compose:${Versions.ACTIVITY_COMPOSE}"
    const val ANDROIDX_COMPOSE_BOM = "androidx.compose:compose-bom:${Versions.COMPOSE_BOM}"
    const val ANDROIDX_COMPOSE_UI = "androidx.compose.ui:ui"
    const val ANDROIDX_COMPOSE_UI_GRAPHICS = "androidx.compose.ui:ui-graphics"
    const val ANDROIDX_COMPOSE_UI_TOOLING = "androidx.compose.ui:ui-tooling"
    const val ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview"
    const val ANDROIDX_COMPOSE_UI_TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest"
    const val ANDROIDX_COMPOSE_UI_TEST_JUNIT4 = "androidx.compose.ui:ui-test-junit4"
    const val ANDROIDX_COMPOSE_MATERIAL3 =
        "androidx.compose.material3:material3:${Versions.MATERIAL3}"
    const val ANDROIDX_NAVIGATION_COMPOSE =
        "androidx.navigation:navigation-compose:${Versions.NAVIGATION_COMPOSE}"
    const val ANDROIDX_COMPOSE_ICON = "androidx.compose.material:material-icons-extended"

    // UNIT TESTING
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val ANDROIDX_JUNIT = "androidx.test.ext:junit:${Versions.JUNIT_EXT}"
    const val ANDROIDX_ESPRESSO_CORE =
        "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"

    // DI
    const val ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE =
        "androidx.lifecycle:lifecycle-runtime-compose:${Versions.LIFECYCLE_RUNTIME_KTX}"
    const val HILT_NAVIGATION_COMPOSE =
        "androidx.hilt:hilt-navigation-compose:${Versions.HILT_NAVIGATION_COMPOSE}"
    const val HILT_ANDROID = "com.google.dagger:hilt-android:${Versions.HILT}"
    const val HILT_COMPILER = "com.google.dagger:hilt-compiler:${Versions.HILT}"

    // LIBRARIES AND TOOLING
    const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
    const val RETROFIT_SERIALIZATION_CONVERTER =
        "com.squareup.retrofit2:converter-kotlinx-serialization:${Versions.RETROFIT}"
    const val KOTLINX_SERIALIZATION_JSON =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLINX_SERIALIZATION}"
    const val CHUCKER = "com.github.chuckerteam.chucker:library:${Versions.CHUCKER}"
    const val CHUCKER_RELEASE = "com.github.chuckerteam.chucker:library-no-op:${Versions.CHUCKER}"
    const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${Versions.LEAK_CANARY}"
    const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"
}
