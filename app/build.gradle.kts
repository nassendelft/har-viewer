import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    macosArm64()
    linuxArm64()

    targets.forEach { (it as? KotlinNativeTarget)?.binaries?.executable() }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":ftxui-kt"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
    }
}