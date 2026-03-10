plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":viewcompose-runtime"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
