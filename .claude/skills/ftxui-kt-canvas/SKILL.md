---
name: ftxui-kt-canvas
description: Canvas — pixel/block-level 2D drawing surface for graphics, plots, and animations.
license: MIT
compatibility: opencode
---

`Canvas` provides a 2D drawing surface. Create it each frame inside a `renderer` callback, draw onto it, then call `toElement()` to produce a renderable `Element`.

Canvas coordinates are in "sub-characters": each terminal cell is 2×4 braille dots or 2×2 block pixels. Actual terminal cell size = `width/2` columns × `height/4` rows (braille) or `width/2` × `height/2` (block).

## Creating a canvas

```kotlin
val c = Canvas(width = 100, height = 100)
```

## Text

```kotlin
c.drawText(x = 0, y = 0, text = "Hello")
c.drawText(x = 10, y = 5, text = "Colored text", color = Color.Red)
```

## Lines

```kotlin
c.drawPointLine(x1, y1, x2, y2)                 // braille-dot line
c.drawPointLine(x1, y1, x2, y2, Color.Blue)      // with color
c.drawBlockLine(x1, y1, x2, y2)                  // block-pixel line
c.drawBlockLine(x1, y1, x2, y2, Color.Green)
```

## Circles

```kotlin
c.drawPointCircle(x, y, radius)          // braille outline
c.drawPointCircleFilled(x, y, radius)    // braille filled
c.drawBlockCircle(x, y, radius)          // block outline
c.drawBlockCircleFilled(x, y, radius)    // block filled
```

## Ellipses

```kotlin
c.drawPointEllipse(x, y, rx, ry)
c.drawPointEllipseFilled(x, y, rx, ry)
c.drawBlockEllipse(x, y, rx, ry)
c.drawBlockEllipseFilled(x, y, rx, ry)
```

## Turning the canvas into an element

```kotlin
val element = c.toElement()
```

## Animated canvas pattern

Create a new `Canvas` each frame so the drawing is fresh:

```kotlin
var mouseX = 0
var mouseY = 0

val canvasComp = renderer {
    val c = Canvas(100, 100)
    c.drawPointLine(mouseX, mouseY, 80, 10, Color.Red)
    c.drawPointLine(80, 10, 50, 80, Color.Blue)
    c.toElement()
}.catchEvent { e ->
    if (e.isMouse) {
        mouseX = (e.mouseX - 1) * 2
        mouseY = (e.mouseY - 1) * 4
    }
    false
}
```

## Plotting a function

```kotlin
val plotComp = renderer {
    val c = Canvas(120, 60)
    c.drawText(0, 0, "sin(x)")
    for (x in 1 until 119) {
        val y1 = (30 + 25 * kotlin.math.sin(x * 0.1)).toInt()
        val y2 = (30 + 25 * kotlin.math.sin((x + 1) * 0.1)).toInt()
        c.drawPointLine(x, y1, x + 1, y2, Color.Green)
    }
    c.toElement()
}
```

## Memory

`Canvas` instances created per-frame do not need explicit cleanup — `toElement()` transfers the data. Only call `c.destroy()` if you create a `Canvas` outside a frame loop and want to release it early.
