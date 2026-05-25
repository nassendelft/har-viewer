plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val hostOs = System.getProperty("os.name")
val hostArch = System.getProperty("os.arch")

val nativeTargetName = (findProperty("native.target") as String?)
    ?: when {
        hostOs.startsWith("Mac") && hostArch == "aarch64" -> "macosArm64"
        hostOs.startsWith("Mac") -> "macosX64"
        hostOs.startsWith("Linux") && hostArch == "aarch64" -> "linuxArm64"
        hostOs.startsWith("Linux") -> "linuxX64"
        else -> error("Unsupported host OS: $hostOs ($hostArch)")
    }

kotlin {
    when (nativeTargetName) {
        "macosArm64" -> macosArm64()
        "macosX64" -> macosX64()
        "linuxArm64" -> linuxArm64()
        "linuxX64" -> linuxX64()
        else -> error("Unsupported target: $nativeTargetName")
    }.binaries.executable {
        when (nativeTargetName) {
            "linuxX64" -> linkerOpts("/usr/lib/x86_64-linux-gnu/libstdc++.a", "-lm")
            "linuxArm64" -> linkerOpts("/usr/aarch64-linux-gnu/lib/libstdc++.a", "-lm")
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
