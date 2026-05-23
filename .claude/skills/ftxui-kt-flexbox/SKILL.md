---
name: ftxui-kt-flexbox
description: flexbox() element with FlexboxConfig — CSS-like flexible box layout for wrapping and aligning elements.
license: MIT
compatibility: opencode
---

`flexbox()` arranges elements in a flex container, similar to CSS flexbox. Use it for wrapping galleries, tag clouds, or any layout that should reflow based on available width.

## Basic usage

```kotlin
val items = words.map { text(it).border() }
flexbox(*items.toTypedArray())
```

With custom config:

```kotlin
flexbox(
    *items.toTypedArray(),
    config = FlexboxConfig(
        direction = FlexboxDirection.Row,
        wrap = FlexboxWrap.Wrap,
        justifyContent = FlexboxJustify.SpaceAround,
        alignItems = FlexboxAlignItems.Center,
        gapX = 1,
        gapY = 0,
    )
)
```

## FlexboxConfig fields

```kotlin
data class FlexboxConfig(
    val direction: FlexboxDirection = FlexboxDirection.Row,
    val wrap: FlexboxWrap = FlexboxWrap.Wrap,
    val justifyContent: FlexboxJustify = FlexboxJustify.FlexStart,
    val alignItems: FlexboxAlignItems = FlexboxAlignItems.Stretch,
    val alignContent: FlexboxAlignContent = FlexboxAlignContent.FlexStart,
    val gapX: Int = 0,
    val gapY: Int = 0,
)
```

## Direction

```
FlexboxDirection.Row             // left → right (default)
FlexboxDirection.RowInversed     // right → left
FlexboxDirection.Column          // top → bottom
FlexboxDirection.ColumnInversed  // bottom → top
```

## Wrap

```
FlexboxWrap.NoWrap        // single line, items may overflow
FlexboxWrap.Wrap          // wrap to next line (default)
FlexboxWrap.WrapInversed  // wrap upward
```

## Justify content (main axis)

```
FlexboxJustify.FlexStart      // pack to start (default)
FlexboxJustify.FlexEnd        // pack to end
FlexboxJustify.Center         // center pack
FlexboxJustify.Stretch        // stretch to fill
FlexboxJustify.SpaceBetween   // equal gaps between items
FlexboxJustify.SpaceAround    // equal space around items
FlexboxJustify.SpaceEvenly    // equal space everywhere
```

## Align items (cross axis per line)

```
FlexboxAlignItems.FlexStart
FlexboxAlignItems.FlexEnd
FlexboxAlignItems.Center
FlexboxAlignItems.Stretch  (default)
```

## Align content (cross axis overall)

Same values as justify, plus `Stretch`. Controls multi-line alignment.

## Example — color palette gallery

```kotlin
renderer {
    val cells = colorInfoSorted2D().flatten().map { info ->
        text(info.name)
            .bgcolor(Color.palette256(info.index256))
            .size(WidthOrHeight.Width, Constraint.Equal, 16)
    }
    flexbox(
        *cells.toTypedArray(),
        config = FlexboxConfig(wrap = FlexboxWrap.Wrap, gapX = 1)
    ).border()
}
```

## gridbox

For a fixed-column grid where you control exact row/column placement:

```kotlin
val rows = listOf(
    listOf(text("A").border(), text("B").border()),
    listOf(text("C").border(), text("D").border()),
)
gridbox(rows)
```
