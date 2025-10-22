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

rootProject.name = "GymTimerApp"
include(":app")
include(":wearapp")
include(":multiplatform:stopwatch")
include(":multiplatform:gymtimer")
include(":shared:connectivity")
include(":shared:persistent:api")
include(":shared:persistent:room")
