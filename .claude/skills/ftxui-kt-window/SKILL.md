---
name: ftxui-kt-window
description: windowComponent() and WindowOptions — a draggable/resizable floating window component.
license: MIT
compatibility: opencode
---

`windowComponent()` creates a floating window that the user can drag and resize with the mouse. Position and size are stored in `IntState` objects.

## Signature

```kotlin
fun windowComponent(options: WindowOptions): Component
```

## WindowOptions

```kotlin
class WindowOptions(
    val inner: Component? = null,     // content component inside the window
    val title: String? = null,        // title bar text
    val left: IntState? = null,       // x position (column), null = use leftDefault
    val top: IntState? = null,        // y position (row), null = use topDefault
    val width: IntState? = null,      // width, null = use widthDefault
    val height: IntState? = null,     // height, null = use heightDefault
    val leftDefault: Int = 0,
    val topDefault: Int = 0,
    val widthDefault: Int = 20,
    val heightDefault: Int = 10,
)
```

## Basic example

```kotlin
val left = IntState(5)
val top = IntState(3)
val width = IntState(30)
val height = IntState(12)

val inner = renderer { text("Window content").center() }

val win = windowComponent(WindowOptions(
    inner = inner,
    title = "My Window",
    left = left,
    top = top,
    width = width,
    height = height,
))

val app = FtxUIApp.fullscreen()
app.loop(win)
```

## Observing position and size

Read the state values to react to user drag/resize:

```kotlin
renderer(win) {
    vbox(
        win.render(),
        text("Position: ${left.value}, ${top.value}"),
        text("Size: ${width.value} × ${height.value}"),
    )
}
```

## Overlapping windows with stacked

Place multiple windows on a `stacked` container so they layer properly:

```kotlin
val stack = stacked()
stack.add(window1)
stack.add(window2)
```

## Memory

Destroy the window component and free all `IntState` instances:

```kotlin
win.destroy()
left.free(); top.free(); width.free(); height.free()
```

The `inner` component is owned by `windowComponent` after construction — do not destroy it separately.
