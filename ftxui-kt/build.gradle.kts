plugins {
    kotlin("multiplatform")
}

val hostOs = System.getProperty("os.name")
val hostArch = System.getProperty("os.arch")

kotlin {
    val nativeTarget = when {
        hostOs.startsWith("Mac") && hostArch == "aarch64" -> macosArm64()
        hostOs.startsWith("Mac") -> macosX64()
        hostOs.startsWith("Linux") && hostArch == "aarch64" -> linuxArm64()
        hostOs.startsWith("Linux") -> linuxX64()
        else -> error("Unsupported host OS: $hostOs ($hostArch)")
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
    commandLine("bash", "-c", "cmake ..")
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

val cinteropTask = when {
    hostOs.startsWith("Mac") && hostArch == "aarch64" -> "cinteropFtxui_cMacosArm64"
    hostOs.startsWith("Mac") -> "cinteropFtxui_cMacosX64"
    hostOs.startsWith("Linux") && hostArch == "aarch64" -> "cinteropFtxui_cLinuxArm64"
    else -> "cinteropFtxui_cLinuxX64"
}
tasks.getByName(cinteropTask).dependsOn("buildFtxuiC")
