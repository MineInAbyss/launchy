import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.kotlinx.serialization)
    alias(idofrontLibs.plugins.jetbrainsCompose)
    alias(idofrontLibs.plugins.compose.compiler)
    alias(idofrontLibs.plugins.dependencyversions)
    alias(idofrontLibs.plugins.version.catalog.update)
    id("de.undercouch.download") version "5.3.1"
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.fabricmc.net")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material", module = "material")
    }
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(idofrontLibs.kotlinx.serialization.json)
    implementation(idofrontLibs.kotlinx.serialization.kaml)
    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.cio.jvm)

    implementation(libs.mpfilepicker)
    implementation(libs.jarchivelib)

    implementation(libs.minecraftAuth)
    implementation(libs.jmccc.mcdownloader)
    implementation(libs.jmccc)
}

idofront {
    setJvmToolchain = false
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
        )
    }
}

val appInstallerName = "Launchy-" + when {
    Os.isFamily(Os.FAMILY_MAC) -> "macOS"
    Os.isFamily(Os.FAMILY_WINDOWS) -> "windows"
    else -> "linux"
}
val appName = "Launchy"

compose.desktop {
    application {
        mainClass = "com.mineinabyss.launchy.MainKt"
        buildTypes.release.proguard {
            configurationFiles.from(
//                project.file("proguard/compose-desktop.pro"),
//                project.file("proguard/gson.pro"),
                project.file("proguard/custom.pro")
            )
            optimize = false
            obfuscate = false
        }

        nativeDistributions {
            when {
                Os.isFamily(Os.FAMILY_MAC) -> targetFormats(TargetFormat.Dmg)
                Os.isFamily(Os.FAMILY_WINDOWS) -> targetFormats(TargetFormat.Exe)
                else -> targetFormats(TargetFormat.AppImage)
            }

            modules(
                "java.instrument",
                "java.management",
                "java.naming",
                "java.security.jgss",
                "jdk.httpserver",
                "jdk.unsupported"
            )
            packageName = appName
            packageVersion = "${project.version}"
            val strippedVersion = project.version.toString().substringBeforeLast("-")
            val iconsRoot = project.file("packaging/icons")
            macOS {
                packageVersion = strippedVersion
                iconFile.set(iconsRoot.resolve("icon.icns"))
            }
            windows {
                packageVersion = strippedVersion
                menu = true
                shortcut = true
                upgradeUuid = "b627d78b-947c-4f5c-9f3b-ae02bfa97d08"
                iconFile.set(iconsRoot.resolve("icon.ico"))
                dirChooser = false
                perUserInstall = false
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}

val linuxAppDir = project.file("packaging/appimage/Launchy.AppDir")
val appImageTool = project.file("deps/appimagetool.AppImage")
val composePackageDir = "$buildDir/compose/binaries/main-release/${
    when {
        Os.isFamily(Os.FAMILY_MAC) -> "dmg"
        Os.isFamily(Os.FAMILY_WINDOWS) -> "exe"
        else -> "app"
    }
}"

tasks {
    val downloadAppImageBuilder by registering(Download::class) {
        src("https://github.com/AppImage/AppImageKit/releases/download/13/appimagetool-x86_64.AppImage")
        dest(appImageTool)
        doLast {
            exec {
                commandLine("chmod", "+x", "deps/appimagetool.AppImage")
            }
        }
    }

    val deleteOldAppDirFiles by registering(Delete::class) {
        delete("$linuxAppDir/usr/bin", "$linuxAppDir/usr/lib")
    }

    val copyBuildToPackaging by registering(Copy::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        dependsOn(deleteOldAppDirFiles)
        from("$buildDir/compose/binaries/main-release/app/$appName")
        into("$linuxAppDir/usr")
    }

    val executeAppImageBuilder by registering(Exec::class) {
        dependsOn(downloadAppImageBuilder)
        dependsOn(copyBuildToPackaging)
        environment("ARCH", "x86_64")
        commandLine(appImageTool, linuxAppDir, "releases/$appInstallerName-${project.version}.AppImage")
    }

    val exeRelease by registering(Copy::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        from(composePackageDir)
        include("*.exe")
        rename("$appName*", appInstallerName)
        into("releases")
    }

    val dmgRelease by registering(Copy::class) {
        dependsOn("packageReleaseDistributionForCurrentOS")
        from(composePackageDir)
        include("*.dmg")
        rename("$appName*", appInstallerName)
        into("releases")
    }

    val packageForRelease by registering {
        mkdir(project.file("releases"))
        when {
            Os.isFamily(Os.FAMILY_WINDOWS) -> dependsOn(exeRelease)
            Os.isFamily(Os.FAMILY_MAC) -> dependsOn(dmgRelease)
            else -> dependsOn(executeAppImageBuilder)
        }
    }
}

versionCatalogUpdate {
    keep {
        keepUnusedPlugins = true
        keepUnusedVersions = true
        keepUnusedLibraries = true
    }
}

tasks {
    dependencyUpdates {
        rejectVersionIf {
            fun isNonStable(version: String): Boolean {
                val unstableKeywords = listOf(
                    "-beta",
                    "-rc",
                    "-alpha",
                )

                return unstableKeywords.any { version.contains(it, ignoreCase = true) }
            }
            isNonStable(candidate.version)
        }
    }
}
