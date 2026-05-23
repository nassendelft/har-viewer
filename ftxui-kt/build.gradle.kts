plugins {
    kotlin("multiplatform")
}

kotlin {
    macosArm64 {
        binaries {
            executable()
        }
        compilations.getByName("main") {
            val ftxui_c by cinterops.creating
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

tasks.getByName("cinteropFtxui_cMacosArm64").dependsOn("buildFtxuiC")
