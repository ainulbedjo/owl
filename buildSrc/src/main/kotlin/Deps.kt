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
        const val COMPOSE_BOM = "2024.09.00"
        const val RETROFIT = "2.11.0"
    }

    object Plugins {
        const val ANDROID_APPLICATION = "com.android.application"
        const val KOTLIN_COMPOSE = "org.jetbrains.kotlin.plugin.compose"
    }

    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val ANDROIDX_LIFECYCLE_RUNTIME_KTX =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_RUNTIME_KTX}"
    const val ANDROIDX_ACTIVITY_COMPOSE =
        "androidx.activity:activity-compose:${Versions.ACTIVITY_COMPOSE}"

    const val ANDROIDX_COMPOSE_BOM = "androidx.compose:compose-bom:${Versions.COMPOSE_BOM}"
    const val ANDROIDX_COMPOSE_UI = "androidx.compose.ui:ui"
    const val ANDROIDX_COMPOSE_UI_GRAPHICS = "androidx.compose.ui:ui-graphics"
    const val ANDROIDX_COMPOSE_UI_TOOLING = "androidx.compose.ui:ui-tooling"
    const val ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview"
    const val ANDROIDX_COMPOSE_UI_TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest"
    const val ANDROIDX_COMPOSE_UI_TEST_JUNIT4 = "androidx.compose.ui:ui-test-junit4"
    const val ANDROIDX_COMPOSE_MATERIAL3 = "androidx.compose.material3:material3"

    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val ANDROIDX_JUNIT = "androidx.test.ext:junit:${Versions.JUNIT_EXT}"
    const val ANDROIDX_ESPRESSO_CORE =
        "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"

    const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
}
