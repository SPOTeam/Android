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
        jcenter()
        maven {
            url = uri("https://devrepo.kakao.com/nexus/content/groups/public/")
        }
        // JitPack 리포지토리
        maven {
            url = uri("https://jitpack.io")
        }

    }
}

rootProject.name = "SPOTeam_android"
include(":app")
 