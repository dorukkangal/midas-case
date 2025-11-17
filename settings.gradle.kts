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

rootProject.name = "midas-case"
include(":app")

// Core modules
include(":core:core-database")
include(":core:core-network")
include(":core:core-ui")
include(":core:core-util")

// Home feature modules
include(":features:home:home-data")
include(":features:home:home-domain")
include(":features:home:home-ui")

// Detail feature modules
include(":features:detail:detail-data")
include(":features:detail:detail-domain")
include(":features:detail:detail-ui")

// Favorites feature modules
include(":features:favorites:favorites-data")
include(":features:favorites:favorites-domain")
include(":features:favorites:favorites-ui")
