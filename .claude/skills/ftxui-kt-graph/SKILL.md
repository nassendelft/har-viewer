---
name: ftxui-kt-graph
description: graph() element and GraphFn — bar/line graph rendering from a callback that fills a height array.
license: MIT
compatibility: opencode
---

`graph()` renders a bar chart from a callback that fills an `IntArray` with column heights.

## GraphFn

Create a `GraphFn` once and keep it alive as long as the graph element is being rendered. Destroy it when you no longer need the graph.

```kotlin
class GraphFn(fn: (width: Int, height: Int, output: IntArray) -> Unit)
```

- `width` — number of columns in the graph
- `height` — total height in rows (use as reference for scaling)
- `output` — fill `output[i]` with the height of column `i` (0 = empty, `height` = full)

## Example — static graph

```kotlin
val fn = GraphFn { width, height, output ->
    for (i in 0 until width) {
        output[i] = (height * (i.toFloat() / width)).toInt()
    }
}

val graphElement = graph(fn)
// Use graphElement inside a renderer:
val comp = renderer {
    graph(fn).border()
}

// Destroy when done:
fn.destroy()
```

## Example — animated sine wave

```kotlin
var phase = 0f
val fn = GraphFn { width, height, output ->
    for (i in 0 until width) {
        val v = 0.5 + 0.5 * kotlin.math.sin(i * 0.2 + phase)
        output[i] = (height * v).toInt()
    }
}

val comp = renderer {
    app.requestAnimationFrame()  // keep re-rendering
    phase += 0.1f
    graph(fn)
        .size(WidthOrHeight.Width, Constraint.GreaterThan, 40)
        .size(WidthOrHeight.Height, Constraint.Equal, 10)
        .border()
}
```

## Producing the element

```kotlin
fun graph(fn: GraphFn): Element
```

Call `graph(fn)` inside the renderer callback to get a fresh element each frame.

## Memory

```kotlin
val fn = GraphFn { w, h, out -> /* ... */ }
// ... use in renderer ...
fn.destroy()  // dispose the Kotlin closure reference
```
