plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val hostOs = System.getProperty("os.name")
val hostArch = System.getProperty("os.arch")

kotlin {
    when {
        hostOs.startsWith("Mac") && hostArch == "aarch64" -> macosArm64()
        hostOs.startsWith("Mac") -> macosX64()
        hostOs.startsWith("Linux") && hostArch == "aarch64" -> linuxArm64()
        hostOs.startsWith("Linux") -> linuxX64()
        else -> error("Unsupported host OS: $hostOs ($hostArch)")
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