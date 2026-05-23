---
name: ftxui-kt-color
description: Color class — palette constants, RGB/HSV constructors, interpolation, and blending in ftxui-kt.
license: MIT
compatibility: opencode
---

`Color` wraps an FTXUI color handle. Use it with `element.color()`, `element.bgcolor()`, and any component option that takes a `Color`.

## Palette constants

```kotlin
Color.Black
Color.Red
Color.Green
Color.Yellow
Color.Blue
Color.Magenta
Color.Cyan
Color.White
Color.Default        // terminal default color
Color.GrayLight
Color.GrayDark
Color.RedLight
Color.GreenLight
Color.YellowLight
Color.BlueLight
Color.MagentaLight
Color.CyanLight
```

## Constructors

```kotlin
Color.rgb(r = 255u, g = 128u, b = 0u)           // true-color RGB
Color.rgba(255u, 128u, 0u, 200u)                 // with alpha
Color.hsv(h = 120u, s = 200u, v = 180u)          // hue/saturation/value
Color.hsva(120u, 200u, 180u, 255u)
Color.palette256(42)                              // xterm 256-color index (Int)
```

## Interpolation and blending

```kotlin
val mid = Color.interpolate(0.5f, Color.Red, Color.Blue)  // 50% between two colors
val mixed = Color.blend(Color.Red, Color.Green)
```

## Inspecting

```kotlin
color.isOpaque()              // true if fully opaque
color.print(isBackground = false)  // ANSI escape string for the color
```

## Lifecycle

Colors created at runtime must be destroyed explicitly:

```kotlin
val c = Color.rgb(200u, 100u, 50u)
text("Hi").color(c)
c.destroy()   // safe to destroy after the element is constructed
```

Palette constants (`Color.Red`, etc.) are shared instances — do not call `destroy()` on them.

## Usage examples

```kotlin
text("Error").color(Color.Red).bold()
text("OK").bgcolor(Color.Green).color(Color.Black)

val highlight = Color.rgb(255u, 215u, 0u)
text("Gold").color(highlight)
highlight.destroy()
```

## 256-color palette reference

Use `colorInfoSorted2D()` to get a structured list of all 256 xterm colors with their names and indices, useful for building color picker UIs:

```kotlin
val palette = colorInfoSorted2D()  // List<List<ColorInfo>>
// ColorInfo(index256: Int, name: String)
```
