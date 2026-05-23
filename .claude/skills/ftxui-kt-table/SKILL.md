---
name: ftxui-kt-table
description: Table and TableSelection — render 2D string data as a styled table element.
license: MIT
compatibility: opencode
---

`Table` renders a `List<List<String>>` as a grid. Apply styling through `TableSelection` before calling `render()`.

## Creating a table

```kotlin
val table = Table(listOf(
    listOf("Name",  "Age", "City"),
    listOf("Alice", "30",  "Amsterdam"),
    listOf("Bob",   "25",  "Berlin"),
    listOf("Carol", "35",  "Copenhagen"),
))
```

## Rendering

```kotlin
val element = table.render()  // Element — embed in a renderer callback
table.destroy()               // free after render() is called
```

## Selecting cells for styling

```kotlin
table.selectAll()              // all cells
table.selectRow(0)             // single row (0-based)
table.selectRows(0, 0)         // row range [from, to] inclusive
table.selectColumn(0)          // single column
table.selectCell(col, row)     // individual cell
```

All selection methods return a `TableSelection`. Chain styling calls, then render.

## TableSelection styling

All methods return `this` (fluent).

```kotlin
selection.border()                           // add border (default Light style)
selection.border(BorderStyle.Heavy)
selection.borderColor(BorderStyle.Light, Color.Blue)
selection.separatorVertical()                // vertical separator lines
selection.separatorVertical(BorderStyle.Dashed)
selection.decorateBold()                     // bold text
selection.decorateCellsAlignRight()          // right-align cell text
selection.decorateCellsColor(Color.Cyan)     // foreground color for cells
selection.decorateCellsColorAlternateRow(Color.GrayDark, modulo = 2, offset = 0)
```

## Common pattern — header row + data rows

```kotlin
val table = Table(
    listOf(listOf("Key", "Value")) + data.map { listOf(it.key, it.value) }
)

// Style the header row:
table.selectRow(0)
    .border(BorderStyle.Heavy)
    .decorateBold()
    .decorateCellsColor(Color.Yellow)

// Add outer border to everything:
table.selectAll().border()

// Alternating row colors on data rows:
table.selectRows(1, data.size)
    .decorateCellsColorAlternateRow(Color.GrayDark, modulo = 2, offset = 1)

val element = table.render()
table.destroy()
```

## Embedding in a renderer

```kotlin
renderer {
    val t = Table(rows)
    t.selectAll().border()
    t.selectRow(0).decorateBold()
    val el = t.render()
    t.destroy()
    el
}
```

## BorderStyle values

`Light`, `Dashed`, `Heavy`, `Double`, `Rounded`, `Empty`
