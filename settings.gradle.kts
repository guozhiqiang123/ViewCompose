pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ViewCompose"
include(":app")
include(":viewcompose-runtime")
include(":viewcompose-renderer")
include(":viewcompose-widget-core")
include(":viewcompose-overlay-android")
include(":viewcompose-image-coil")
include(":viewcompose-benchmark")
include(":viewcompose-lifecycle")
include(":viewcompose-viewmodel")
 
