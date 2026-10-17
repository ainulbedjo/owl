// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Deps.Plugins.ANDROID_APPLICATION) version Deps.Versions.AGP apply false
    id(Deps.Plugins.KOTLIN_COMPOSE) version Deps.Versions.KOTLIN apply false
    id("com.android.library") version "9.1.0" apply false
}
