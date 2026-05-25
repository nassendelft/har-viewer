plugins {
    kotlin("multiplatform")
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
    val nativeTarget = when (nativeTargetName) {
        "macosArm64" -> macosArm64()
        "macosX64" -> macosX64()
        "linuxArm64" -> linuxArm64()
        "linuxX64" -> linuxX64()
        else -> error("Unsupported target: $nativeTargetName")
    }

    nativeTarget.apply {
        binaries.executable()

        compilations.getByName("main") {
            val ftxui_c by cinterops.creating {
                includeDirs(project.file("../binding/ftxui_c"))
                extraOpts(
                    "-libraryPath", project.file("../binding/ftxui_c/build").absolutePath,
                    "-libraryPath", project.file("../binding/ftxui_c/build/ftxui_build").absolutePath
                )
            }
        }
    }
}

val bindingDir = project.file("../binding/ftxui_c")
val cmakeBuildDir = project.file("../binding/ftxui_c/build")

tasks.register<Exec>("configureFtxuiC") {
    inputs.file(bindingDir.resolve("CMakeLists.txt"))
    outputs.file(cmakeBuildDir.resolve("CMakeCache.txt"))
    workingDir = cmakeBuildDir
    environment = System.getenv().toMutableMap() as Map<String, Any>
    val cmakeArgs = buildList {
        add("cmake")
        add("..")
        if (nativeTargetName == "linuxArm64" && hostArch != "aarch64") {
            add("-DCMAKE_C_COMPILER=aarch64-linux-gnu-gcc")
            add("-DCMAKE_CXX_COMPILER=aarch64-linux-gnu-g++")
        }
    }
    commandLine(cmakeArgs)
    doFirst {
        cmakeBuildDir.mkdirs()
    }
}

tasks.register<Exec>("buildFtxuiC") {
    inputs.files(fileTree(bindingDir) { include("*.cpp", "*.h", "CMakeLists.txt") })
    outputs.file(cmakeBuildDir.resolve("libftxui_c_binding.a"))
    workingDir = cmakeBuildDir
    environment = System.getenv().toMutableMap() as Map<String, Any>
    commandLine("bash", "-c", "cmake --build . --target ftxui_c_binding_static")
    dependsOn("configureFtxuiC")
}

val cinteropTask = "cinteropFtxui_c${nativeTargetName.replaceFirstChar { it.uppercase() }}"
tasks.getByName(cinteropTask).dependsOn("buildFtxuiC")
