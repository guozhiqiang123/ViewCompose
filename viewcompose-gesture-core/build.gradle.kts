plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":viewcompose-ui-contract"))
    testImplementation(libs.junit)
}
