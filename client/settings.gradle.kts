pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ( url = "https://devrepo.kakao.com/nexus/content/groups/public/" )
    }
}

rootProject.name = "MoveMove-Android"
include(":presentation")
include(":data")
include(":domain")
