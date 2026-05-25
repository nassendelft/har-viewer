plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val hostOs = System.getProperty("os.name")

kotlin {
    when {
        hostOs.startsWith("Mac") -> macosArm64()
        hostOs.startsWith("Linux") -> linuxArm64()
        else -> error("Unsupported host OS: $hostOs")
    }.binaries.executable()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":ftxui-kt"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
    }
}