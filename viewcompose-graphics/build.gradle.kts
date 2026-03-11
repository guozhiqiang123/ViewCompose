plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.viewcompose.graphics"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(project(":viewcompose-graphics-core"))
    implementation(project(":viewcompose-runtime"))
    implementation(project(":viewcompose-ui-contract"))
    implementation(project(":viewcompose-widget-core"))
    testImplementation(libs.junit)
}
