---
name: ftxui-kt-dropdown
description: dropdown() and dropdownCustom() components — collapsible selection lists.
license: MIT
compatibility: opencode
---

## Basic dropdown

Shows a checkbox (open/close toggle) and a radiobox (entry list) that expands when opened.

```kotlin
val entries = listOf("Apple", "Banana", "Cherry")
val selected = IntState(0)
val dropComp = dropdown(entries, selected)

// Property ref:
var selectedIndex = 0
val dropComp = dropdown(entries, ::selectedIndex)
```

## Reading the value

```kotlin
renderer(dropComp) {
    vbox(
        dropComp.render(),
        separator(),
        text("Chosen: ${entries[selected.value]}"),
    ).border()
}
```

## Custom dropdown

Full control over the rendered layout and each entry's appearance.

```kotlin
fun dropdownCustom(
    entries: List<String>,
    selected: IntState? = null,
    transform: ((open: Boolean, checkbox: Element, radiobox: Element) -> Element)? = null,
    entryTransform: ((EntryState) -> Element)? = null,
): Component
```

- `transform` — controls how the checkbox (toggle) and radiobox (list) are combined. Called each frame with the open state and pre-rendered sub-elements.
- `entryTransform` — controls how each individual entry is rendered. Receives `EntryState` (label, state, active, focused, index).

```kotlin
val customDrop = dropdownCustom(
    entries = listOf("Red", "Green", "Blue"),
    selected = selected,
    transform = { open, checkboxEl, radioboxEl ->
        vbox(
            checkboxEl.bold(),
            if (open) radioboxEl.borderLight() else emptyElement(),
        )
    },
    entryTransform = { s ->
        text(if (s.focused) "> ${s.label}" else "  ${s.label}")
            .color(if (s.active) Color.Yellow else Color.White)
    }
)
```

`EntryState` fields:
- `label: String`
- `state: Boolean` — entry is selected
- `active: Boolean` — mouse hover
- `focused: Boolean` — keyboard focus
- `index: Int` — position in the list

## Memory

Free `IntState` after destroying the component. Property-ref overloads handle memory automatically.
