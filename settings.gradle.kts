pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        google()
        maven("https://maven.hq.hydraulic.software")
        mavenLocal()
    }
}

dependencyResolutionManagement {
    val idofrontVersion: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
    }

    versionCatalogs {
        create("idofrontLibs") {
            from("com.mineinabyss:catalog:$idofrontVersion")
            version("kotlin", "1.9.22")
            version("compose", "1.6.0")
        }
    }
}

rootProject.name = "launchy"

