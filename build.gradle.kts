import Com_mineinabyss_conventions_platform_gradle.Deps
import org.codehaus.plexus.util.Os
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("org.jetbrains.compose") version "1.0.1"
    kotlin("plugin.serialization")
//    id("com.github.johnrengelman.shadow") version "7.1.1"
//    id("proguard") version "7.1.0"

}

/*buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.1.0")
    }
}*/

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.fabricmc.net")
}

dependencies {
    implementation(files("deps/BrowserLauncher2-all-1_3.jar"))
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(Deps.kotlinx.serialization.json)
    implementation(Deps.kotlinx.serialization.kaml)
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")

    implementation("org.json:json:20210307")
    implementation("net.fabricmc:fabric-installer:0.9.0")
    implementation("edu.stanford.ejalbert:BrowserLauncher2:1.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

compose.desktop {
    application {
        mainClass = "com.mineinabyss.launchy.MainKt"
        nativeDistributions {
            if (Os.isFamily(Os.FAMILY_MAC))
                targetFormats(TargetFormat.Dmg)
            else
                targetFormats(TargetFormat.AppImage)
            modules("java.instrument", "jdk.unsupported")
            packageName = "launchy"
            packageVersion = "${project.version}"
        }
    }
}

/*
tasks {
    shadowJar {
        mergeServiceFiles()
        minimize {
            exclude(dependency("org.jetbrains.compose.desktop:desktop-jvm.*:.*"))
            exclude(dependency("io.ktor:ktor-client.*:.*"))
            exclude(dependency(("org.jetbrains.compose.material:material-icons.*:.*")))
            exclude("androidx/compose/material/icons/filled/**")
            exclude("androidx/compose/material/icons/outlined/**")
            exclude("androidx/compose/material/icons/sharp/**")
            exclude("androidx/compose/material/icons/twotone/**")
        }

        manifest {
            attributes(mapOf("Main-Class" to "com.mineinabyss.launchy.MainKt"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
