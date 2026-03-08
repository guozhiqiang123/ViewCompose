// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

val modulePackageRoots = mapOf(
    "app" to "com.viewcompose",
    "viewcompose-runtime" to "com.viewcompose.runtime",
    "viewcompose-ui-contract" to "com.viewcompose.ui",
    "viewcompose-renderer" to "com.viewcompose.renderer",
    "viewcompose-widget-core" to "com.viewcompose.widget.core",
    "viewcompose-host-android" to "com.viewcompose.host.android",
    "viewcompose-overlay-android" to "com.viewcompose.overlay.android",
    "viewcompose-image-coil" to "com.viewcompose.image.coil",
    "viewcompose-benchmark" to "com.viewcompose.benchmark",
    "viewcompose-lifecycle" to "com.viewcompose.lifecycle",
    "viewcompose-viewmodel" to "com.viewcompose.viewmodel",
)

val qaQuickTasks = listOf(
    ":viewcompose-runtime:compileDebugKotlin",
    ":viewcompose-ui-contract:compileKotlin",
    ":viewcompose-host-android:compileDebugKotlin",
    ":viewcompose-lifecycle:compileDebugKotlin",
    ":viewcompose-viewmodel:compileDebugKotlin",
    ":viewcompose-renderer:compileDebugKotlin",
    ":viewcompose-widget-core:compileDebugKotlin",
    ":viewcompose-overlay-android:compileDebugKotlin",
    ":viewcompose-image-coil:compileDebugKotlin",
    ":app:compileDebugKotlin",
    ":viewcompose-runtime:testDebugUnitTest",
    ":viewcompose-ui-contract:test",
    ":viewcompose-host-android:testDebugUnitTest",
    ":viewcompose-lifecycle:testDebugUnitTest",
    ":viewcompose-viewmodel:testDebugUnitTest",
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
