pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io")
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

rootProject.name = "Country picker"
include(":app")
include(":country_picker")
