import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.kotlinx.serialization)
    alias(idofrontLibs.plugins.compose)
    id("de.undercouch.download") version "5.3.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-cio:2.3.8")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.8")

    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    implementation("org.rauschig:jarchivelib:1.2.0")

    implementation("net.raphimc:MinecraftAuth:4.0.0")
    implementation("dev.3-3:jmccc-mcdownloader:3.1.4")
    implementation("dev.3-3:jmccc:3.1.4")
//    implementation("dev.3-3:jmccc-microsoft-authenticator:3.1.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf(
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
    )
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

            modules("java.instrument", "java.management", "java.naming", "java.security.jgss", "jdk.httpserver", "jdk.unsupported")
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
