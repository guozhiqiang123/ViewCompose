plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":viewcompose-graphics-core"))
    testImplementation(libs.junit)
}
