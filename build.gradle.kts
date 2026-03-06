// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

val qaQuickTasks = listOf(
    ":ui-runtime:compileDebugKotlin",
    ":ui-renderer:compileDebugKotlin",
    ":ui-widget-core:compileDebugKotlin",
    ":ui-overlay-android:compileDebugKotlin",
    ":ui-image-coil:compileDebugKotlin",
    ":app:compileDebugKotlin",
    ":ui-runtime:testDebugUnitTest",
    ":ui-renderer:testDebugUnitTest",
    ":ui-widget-core:testDebugUnitTest",
    ":ui-overlay-android:testDebugUnitTest",
    ":ui-image-coil:testDebugUnitTest",
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
