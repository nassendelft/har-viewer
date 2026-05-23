---
name: ftxui-kt-menu
description: menu(), menuHorizontal(), menuToggle(), menuEntry(), and animated menu components.
license: MIT
compatibility: opencode
---

Menus present a list of selectable entries. The selected index is stored in `IntState` or a `KMutableProperty0<Int>`.

## Vertical menu

```kotlin
val entries = listOf("New", "Open", "Save", "Quit")
val selected = IntState(0)
val menuComp = menu(entries, selected)

// Property ref:
var selectedIndex = 0
val menuComp = menu(entries, ::selectedIndex)
```

## Horizontal menu (tab bar)

```kotlin
val tabs = listOf("File", "Edit", "View")
val selected = IntState(0)
val hMenu = menuHorizontal(tabs, selected)
```

## Horizontal animated menu

Entries animate between active/inactive with smooth color transitions.

```kotlin
val hAnimMenu = menuHorizontalAnimated(tabs, selected)
```

## Toggle menu (animated)

Like `menuHorizontalAnimated` but renders entries as toggles:

```kotlin
val toggleMenu = menuToggle(tabs, selected)
```

## Custom menu entry

Individual entries with optional animated colors:

```kotlin
val entry = menuEntry("Dashboard")

val coloredEntry = menuEntry(
    "Settings",
    animatedMenuEntryColors(
        bgActive = Color.Blue,
        bgInactive = Color.GrayDark,
        fgActive = Color.White,
        fgInactive = Color.GrayLight
    )
)
```

`animatedMenuEntryColors` parameters:
- `bgActive` — background when selected
- `bgInactive` — background when not selected (default: `Color.Black`)
- `fgActive` — foreground when selected (default: `Color.White`)
- `fgInactive` — foreground when not selected (default: `bgActive`)

Add custom entries to a `horizontal` or `vertical` container manually.

## Reading the selection

```kotlin
renderer(menuComp) {
    vbox(
        menuComp.render(),
        separator(),
        text("Selected: ${entries[selected.value]}"),
    ).border()
}
```

## Sidebar + content pattern

```kotlin
val pages = listOf("Overview", "Details", "Settings")
val selected = IntState(0)
val sidebar = menu(pages, selected)

val content = renderer {
    text("Content for: ${pages[selected.value]}").flex()
}

val layout = horizontal(sidebar, content)
val r = renderer(layout) {
    hbox(
        sidebar.render().size(WidthOrHeight.Width, Constraint.Equal, 12),
        separatorLight(),
        content.render().flex(),
    ).border()
}
```

## Memory

Free `IntState` after destroying the component, or use property-ref overloads.
