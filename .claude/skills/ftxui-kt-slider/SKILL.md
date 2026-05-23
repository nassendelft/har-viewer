---
name: ftxui-kt-slider
description: slider() component — horizontal and directional sliders for Int and Float values.
license: MIT
compatibility: opencode
---

## Int slider (horizontal, with label)

```kotlin
fun slider(label: String, value: IntState, min: Int, max: Int, increment: Int = 1): Component
fun slider(label: String, value: KMutableProperty0<Int>, min: Int, max: Int, increment: Int = 1): Component
```

```kotlin
val volume = IntState(50)
val volumeSlider = slider("Volume", volume, min = 0, max = 100, increment = 5)
```

## Int slider (directional, no label)

```kotlin
fun slider(value: IntState, min: Int, max: Int, increment: Int = 1, direction: Direction): Component
```

```kotlin
val brightness = IntState(75)
val vertSlider = slider(brightness, 0, 100, 5, Direction.Up)
```

## Float slider (horizontal, with label)

```kotlin
fun slider(label: String, value: FloatState, min: Float, max: Float, increment: Float): Component
```

```kotlin
val opacity = FloatState(1.0f)
val opacitySlider = slider("Opacity", opacity, 0f, 1f, 0.1f)
```

## Float slider (directional, with optional colors)

```kotlin
fun slider(
    value: FloatState,
    min: Float,
    max: Float,
    increment: Float,
    direction: Direction,
    colorActive: Color? = null,
    colorInactive: Color? = null
): Component
```

```kotlin
val level = FloatState(0.5f)
val coloredSlider = slider(
    level, 0f, 1f, 0.05f, Direction.Right,
    colorActive = Color.Green,
    colorInactive = Color.GrayDark
)
```

## Direction enum

`Direction.Up`, `Direction.Down`, `Direction.Left`, `Direction.Right`

## Reading the value

```kotlin
renderer(sliderComp) {
    vbox(
        text("Volume: ${volume.value}"),
        sliderComp.render(),
    ).border()
}
```

## Memory

Free `IntState` or `FloatState` after destroying the component. Property-ref overloads manage memory automatically.

```kotlin
sliderComp.destroy()
volume.free()
```
