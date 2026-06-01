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

val stbImageBuildDir = project.file("build/stb_image")

tasks.register<Exec>("buildStbImage") {
    val srcFile = project.file("src/nativeInterop/stb_image/stb_image_impl.c")
    val hFile = project.file("src/nativeInterop/stb_image/stb_image.h")
    inputs.files(srcFile, hFile)
    outputs.file(stbImageBuildDir.resolve("libstb_image.a"))
    doFirst { stbImageBuildDir.mkdirs() }
    val cc = when {
        nativeTargetName == "linuxArm64" && hostArch != "aarch64" -> "aarch64-linux-gnu-gcc"
        else -> "cc"
    }
    commandLine(
        "bash", "-c",
        "$cc -c ${srcFile.absolutePath} -o ${stbImageBuildDir}/stb_image_impl.o && " +
            "ar rcs ${stbImageBuildDir}/libstb_image.a ${stbImageBuildDir}/stb_image_impl.o"
    )
}

kotlin {
    val nativeTarget = when (nativeTargetName) {
        "macosArm64" -> macosArm64()
        "linuxArm64" -> linuxArm64()
        "linuxX64" -> linuxX64()
        else -> error("Unsupported target: $nativeTargetName")
    }

    nativeTarget.binaries.executable {
        when (nativeTargetName) {
            "linuxX64" -> linkerOpts("/usr/lib/gcc/x86_64-linux-gnu/11/libstdc++.a", "-lm")
            "linuxArm64" -> linkerOpts(
                "/usr/lib/gcc-cross/aarch64-linux-gnu/11/libstdc++.a",
                "/usr/lib/gcc-cross/aarch64-linux-gnu/11/libgcc.a",
                "-lm"
            )
        }
    }

    nativeTarget.compilations.getByName("main") {
        val stb_image by cinterops.creating {
            includeDirs(project.file("src/nativeInterop/stb_image"))
            extraOpts("-libraryPath", stbImageBuildDir.absolutePath)
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("nl.ncaj.ftxui:ftxui-kt:1.0.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            }
        }
    }
}

val cinteropStbTask = "cinteropStb_image${nativeTargetName.replaceFirstChar { it.uppercase() }}"
tasks.getByName(cinteropStbTask).dependsOn("buildStbImage")
