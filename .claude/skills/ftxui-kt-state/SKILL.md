---
name: ftxui-kt-state
description: State holder classes (BoolState, IntState, StringState, FloatState) for bridging Kotlin values into native FTXUI component state.
license: MIT
compatibility: opencode
---

Interactive components (checkbox, slider, menu, input, etc.) need a native-heap-backed buffer that FTXUI can read/write. The state classes wrap these buffers.

## Classes

```kotlin
class BoolState(initial: Boolean = false)
class IntState(initial: Int = 0)
class StringState(initial: String = "")
class FloatState(initial: Float = 0f)
```

Each has a `value` property for reading and writing:

```kotlin
val checked = BoolState(false)
checked.value = true
println(checked.value)  // true
```

Call `free()` to release the native allocation when you no longer need it:

```kotlin
checked.free()
```

## Usage with components

Pass state objects to component factory functions:

```kotlin
val selected = IntState(0)
val menuComp = menu(entries, selected)

val content = StringState()
val inputComp = input(content, "placeholder")

val isChecked = BoolState()
val checkComp = checkbox("Enable feature", isChecked)
```

Read the current value anywhere — it reflects what the user has entered:

```kotlin
renderer(component) {
    vbox(
        text("Selected: ${selected.value}"),
        component.render()
    )
}
```

## Property reference overloads (no explicit state)

If your state already lives in a Kotlin variable, use the `KMutableProperty0` overloads with `::myVar`. The native buffer is created and freed automatically:

```kotlin
var selectedIndex = 0
val menuComp = menu(entries, ::selectedIndex)

var isOpen = false
val collapsibleComp = collapsible("Section", inner, ::isOpen)
```

The sync rules per frame:
- If the Kotlin variable changed since last frame → pushes to native (Kotlin wins).
- Otherwise if FTXUI changed the native value → pulls to Kotlin.

## Memory lifecycle

- `StringState` uses a C++ string allocation; free with `free()`.
- `BoolState`, `IntState`, `FloatState` use `nativeHeap`; free with `free()`.
- When a component created with the property-ref overload is destroyed, it frees the native buffer automatically.
- When you pass an explicit `BoolState`/`IntState`/etc. to a component, you own it — call `free()` yourself after destroying the component.

## Example — explicit state lifecycle

```kotlin
val selected = IntState(0)
val tabComp = tab(selected)
// ... add children, run app ...
tabComp.destroy()
selected.free()
```
