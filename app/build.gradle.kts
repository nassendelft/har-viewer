plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    macosArm64 {
        binaries {
            executable()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":ftxui-kt"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            }
        }
    }
}