---
name: ftxui-kt-selection
description: checkbox(), radiobox(), and toggle() components for boolean and single-choice selection.
license: MIT
compatibility: opencode
---

## Checkbox

Single boolean toggle. Displays `[x]` / `[ ]` beside a label.

```kotlin
// Explicit state:
val checked = BoolState(false)
val check = checkbox("Enable feature", checked)

// Property reference (no manual state lifecycle):
var isEnabled = false
val check = checkbox("Enable feature", ::isEnabled)
```

Reading the value:

```kotlin
text(if (checked.value) "ON" else "OFF")
```

## Radiobox

One-of-N selection from a list of strings. Selected index is 0-based.

```kotlin
// Explicit state:
val entries = listOf("Option A", "Option B", "Option C")
val selected = IntState(0)
val radio = radiobox(entries, selected)

// Property reference:
var selectedIndex = 0
val radio = radiobox(entries, ::selectedIndex)
```

## Toggle

Horizontal tabs for one-of-N selection (like a segmented control).

```kotlin
val entries = listOf("Tab 1", "Tab 2", "Tab 3")
val selected = IntState(0)
val toggleComp = toggle(entries, selected)

// Property reference:
var tab = 0
val toggleComp = toggle(entries, ::tab)
```

## Layout example — checkbox group

```kotlin
val options = listOf("Bold", "Italic", "Underline")
val states = options.map { BoolState(false) }
val checks = options.mapIndexed { i, label -> checkbox(label, states[i]) }
val group = vertical(*checks.toTypedArray())

val r = renderer(group) {
    vbox(
        text("Text style:"),
        group.render(),
        separator(),
        text("Selected: ${options.filterIndexed { i, _ -> states[i].value }.joinToString()}"),
    ).border()
}
```

## Layout example — radiobox + content

```kotlin
val pages = listOf("Home", "Settings", "About")
val selected = IntState(0)
val radio = radiobox(pages, selected)

val r = renderer(radio) {
    hbox(
        radio.render().size(WidthOrHeight.Width, Constraint.Equal, 15),
        separatorLight(),
        text("Page: ${pages[selected.value]}").flex(),
    ).border()
}
```

## Memory

- `BoolState` and `IntState` must be freed with `free()` after the component is destroyed.
- Property-ref overloads manage native memory automatically.
