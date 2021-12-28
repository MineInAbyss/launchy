import Com_mineinabyss_conventions_platform_gradle.Deps
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.publication")
    id("org.jetbrains.compose") version "1.0.1"
    kotlin("plugin.serialization")
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.fabricmc.net")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(Deps.kotlinx.serialization.json)
    implementation(Deps.kotlinx.serialization.kaml)
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")

    implementation("org.json:json:20210307")
    implementation("net.fabricmc:fabric-installer:0.9.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "com.mineinabyss.launchy.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.AppImage)
            packageName = "launchy"
            packageVersion = "1.0.0"
        }
    }
}
