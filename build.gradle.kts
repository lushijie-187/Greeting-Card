// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 声明 Android Application 插件可用，但不在项目根目录应用它
    alias(libs.plugins.android.application) apply false

    // 声明 Kotlin Android 插件可用，但不在项目根目录应用它
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
