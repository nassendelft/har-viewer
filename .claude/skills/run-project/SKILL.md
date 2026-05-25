---
name: run
description: Run the har-viewer app with a given HAR file — builds the native binary if needed, then launches it.
license: MIT
compatibility: opencode
---

This project produces a native TUI binary via Kotlin/Native. The app requires a HAR file path as its sole argument.

## Binary location

```
app/build/bin/macosArm64/debugExecutable/app.kexe
```

## Build the binary (if not already built)

```bash
./gradlew :app:linkDebugExecutableMacosArm64
```

## Run with a HAR file

```bash
./app/build/bin/macosArm64/debugExecutable/app.kexe <path-to-file.har>
```

Example using the bundled test file:

```bash
./app/build/bin/macosArm64/debugExecutable/app.kexe test.har
```

## Build and run in one step

```bash
./gradlew :app:linkDebugExecutableMacosArm64 && ./app/build/bin/macosArm64/debugExecutable/app.kexe <path-to-file.har>
```

## Notes

- The app will print `Usage: har-viewer <file.har>` and exit if no argument is given.
- It will print an error and exit if the HAR file cannot be parsed.
- The app is a fullscreen TUI — it takes over the terminal. Press `q` or `Ctrl-C` to quit.
- If the user does not specify a HAR file, default to `test.har` in the project root.