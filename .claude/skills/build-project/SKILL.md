---
name: build
description: Build the har-viewer project using Gradle — ftxui-kt module and app module build commands.
license: MIT
compatibility: opencode
---

This project uses Gradle with Kotlin Multiplatform targeting `macosArm64`. The build compiles the `ftxui-kt` binding module first, then the `app` module.

## Build commands

Build just the ftxui-kt binding module:
```bash
./gradlew :ftxui-kt:build
```

Build just the app module (also builds ftxui-kt as a dependency):
```bash
./gradlew :app:build
```

Build everything:
```bash
./gradlew build
```

## Compile and link the native executable

The app produces a native binary via Kotlin/Native. To compile the executable:
```bash
./gradlew :app:linkDebugExecutableMacosArm64
```

The output binary is at:
```
app/build/bin/macosArm64/debugExecutable/app.kexe
```

## Run the app

```bash
./app/build/bin/macosArm64/debugExecutable/app.kexe
```

Or in one step:
```bash
./gradlew :app:linkDebugExecutableMacosArm64 && ./app/build/bin/macosArm64/debugExecutable/app.kexe
```

## How the build works

- `ftxui-kt` uses a cinterop (`ftxui_c`) that depends on a CMake-built C++ static library (`libftxui_c_binding.a`) in `binding/ftxui_c/build/`.
- Gradle tasks `configureFtxuiC` → `buildFtxuiC` → `cinteropFtxui_cMacosArm64` run automatically in order when building `ftxui-kt`.
- `app` depends on `:ftxui-kt` and `kotlinx-serialization-json`.