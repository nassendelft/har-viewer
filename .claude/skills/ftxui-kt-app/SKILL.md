---
name: ftxui-kt-app
description: FtxUIApp, FtxUILoop, and FtxUIEvent — application lifecycle and event loop in ftxui-kt.
license: MIT
compatibility: opencode
---

`FtxUIApp` is the entry point. Create one, call `loop()` with a root component, and destroy it when done.

## Screen modes

```kotlin
val app = FtxUIApp.fullscreen()       // Takes over the full terminal
val app = FtxUIApp.fitComponent()     // Resizes to fit the root component
val app = FtxUIApp.terminalOutput()   // Renders once to stdout and exits
```

## Running the loop

```kotlin
val app = FtxUIApp.fitComponent()
app.loop(rootComponent)   // Blocks until the user exits
app.destroy()
```

## Exiting programmatically

```kotlin
val exitApp = app.exitClosure()       // () -> Unit, safe to pass to button callbacks
button("Quit", exitApp)

// Or call directly:
app.exit()
```

## Clipboard selection

```kotlin
app.selectionChange { 
    val selected = app.getSelection()
    // handle clipboard text
}
```

## Manual loop control with FtxUILoop

Use `FtxUILoop` when you need to interleave FTXUI rendering with other work (e.g. polling a queue, sleeping between ticks).

```kotlin
val loop = FtxUILoop(app, component)
while (!loop.hasQuitted()) {
    loop.runOnce()              // process events and render one frame
    platform.posix.usleep(10_000u)
}
loop.destroy()
app.destroy()
component.destroy()
```

## FtxUIEvent

`catchEvent` on a component receives `FtxUIEvent`:

```kotlin
data class FtxUIEvent(
    val input: String,         // raw escape sequence
    val debugString: String,
    val isCharacter: Boolean,
    val character: String,     // single character if isCharacter
    val isMouse: Boolean,
    val mouseX: Int,           // terminal column (1-indexed)
    val mouseY: Int,           // terminal row (1-indexed)
)
```

Example — quit on 'q', track mouse position:

```kotlin
component.catchEvent { event ->
    when {
        event.character == "q" -> { app.exit(); true }
        event.isMouse -> { mouseX = event.mouseX; mouseY = event.mouseY; false }
        else -> false
    }
}
```

Return `true` to consume the event (stop propagation), `false` to let it bubble.

## Animation

Call `requestAnimationFrame()` inside a render callback to keep re-rendering every frame:

```kotlin
val animComponent = renderer {
    app.requestAnimationFrame()
    // build animated element...
}
```

## Memory notes

- Call `app.destroy()` after the loop finishes.
- Components passed to `loop()` are NOT destroyed automatically — destroy them yourself.
- `FtxUILoop.destroy()` destroys the loop handle only; destroy the app and component separately.
