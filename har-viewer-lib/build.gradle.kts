plugins {
    kotlin("multiplatform")
}

kotlin {
    macosArm64 {
        binaries {
            executable {
                linkerOpts("-L/Users/nick/Projects/har-viewer/binding/ftxui_c/build", "-lftxui_c_binding", "-Wl,-rpath,@executable_path/../Frameworks")
            }
        }
        compilations.getByName("main") {
            val ftxui_c by cinterops.creating {
                definitionFile.set(project.file("../binding/ftxui_c/ftxui_c.def"))
                includeDirs("../binding/ftxui_c")
            }
        }
    }
}

val libPath = project.file("../binding/ftxui_c/build/libftxui_c_binding.dylib")
val frameworkDir = layout.buildDirectory.dir("bin/macosArm64/Frameworks")

tasks.register<Exec>("configureFtxuiC") {
    workingDir = project.file("../binding/ftxui_c/build")
    environment = System.getenv().toMutableMap() as Map<String, Any>
    commandLine("bash", "-c", "cmake ..")
    doFirst {
        project.file("../binding/ftxui_c/build").mkdirs()
    }
}

tasks.register<Exec>("buildFtxuiC") {
    workingDir = project.file("../binding/ftxui_c/build")
    environment = System.getenv().toMutableMap() as Map<String, Any>
    commandLine("bash", "-c", "cmake --build .")
    dependsOn("configureFtxuiC")
}

tasks.register<Copy>("copyFtxuiLib") {
    from(libPath)
    into(frameworkDir)
    dependsOn("buildFtxuiC") // Make copyFtxuiLib depend on the new buildFtxuiC task
    dependsOn(project.tasks.named("cinteropFtxui_cMacosArm64"))
}

tasks.getByName("linkDebugExecutableMacosArm64").dependsOn("copyFtxuiLib")