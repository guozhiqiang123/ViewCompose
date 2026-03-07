// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

val qaQuickTasks = listOf(
    ":viewcompose-runtime:compileDebugKotlin",
    ":viewcompose-renderer:compileDebugKotlin",
    ":viewcompose-widget-core:compileDebugKotlin",
    ":viewcompose-overlay-android:compileDebugKotlin",
    ":viewcompose-image-coil:compileDebugKotlin",
    ":app:compileDebugKotlin",
    ":viewcompose-runtime:testDebugUnitTest",
    ":viewcompose-renderer:testDebugUnitTest",
    ":viewcompose-widget-core:testDebugUnitTest",
    ":viewcompose-overlay-android:testDebugUnitTest",
    ":viewcompose-image-coil:testDebugUnitTest",
    ":app:testDebugUnitTest",
)

tasks.register("qaQuick") {
    group = "verification"
    description = "Run compile + unit-test quality gate for all core modules."
    dependsOn(qaQuickTasks)
}

tasks.register("qaFull") {
    group = "verification"
    description = "Run qaQuick plus connected UI tests on device/emulator."
    dependsOn("qaQuick", ":app:connectedDebugAndroidTest")
}
