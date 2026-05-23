---
name: ftxui-kt-containers
description: Container components — horizontal, vertical, tab, stacked, collapsible, maybe, modal, and resizableSplit.
license: MIT
compatibility: opencode
---

## horizontal / vertical

Arrange child components side-by-side or top-to-bottom. Tab key moves focus between children.

```kotlin
val layout = horizontal(buttonA, buttonB, buttonC)
val layout = vertical(inputName, inputEmail, submitButton)
```

Add children dynamically with `ContainerComponent.add()`:

```kotlin
val container = vertical()
for (item in items) container.add(makeItemComponent(item))
```

`add()` transfers ownership — do not call `destroy()` on children after adding.

## tab

Show only the child at the selected index. Use with a menu or toggle to build tabbed UIs.

```kotlin
val selected = IntState(0)
val tabs = tab(selected)
tabs.add(page1Component)
tabs.add(page2Component)
tabs.add(page3Component)

val tabMenu = toggle(listOf("Page 1", "Page 2", "Page 3"), selected)
val layout = vertical(tabMenu, tabs)
```

## stacked

All children are rendered on top of each other. The last added child receives events first.

```kotlin
val stack = stacked()
stack.add(backgroundComp)
stack.add(overlayComp)
```

## collapsible

A labeled section that can be expanded or collapsed.

```kotlin
val show = BoolState(true)
val section = collapsible("Advanced Options", innerComponent, show)

// Property ref:
var isOpen = true
val section = collapsible("Advanced Options", innerComponent, ::isOpen)
```

## maybe

Show or hide a component entirely. Useful for conditional UI.

```kotlin
val show = BoolState(false)
val conditional = maybe(innerComponent, show)

// Extension form:
val conditional = innerComponent.maybe(show)

// Property ref:
var visible = false
val conditional = innerComponent.maybe(::visible)
```

## modal

Overlay a modal dialog on top of a main component. The modal receives all input when visible.

```kotlin
val showModal = BoolState(false)
val combined = modal(mainComponent, modalComponent, showModal)

// Extension form:
val combined = mainComponent.modal(modalComponent, showModal)
```

Typical pattern:

```kotlin
val showModal = BoolState(false)

val mainButtons = vertical(
    button("Open modal") { showModal.value = true },
    button("Quit", app.exitClosure()),
)
val mainComponent = renderer(mainButtons) {
    vbox(text("Main"), mainButtons.render()).border().center()
}

val modalButtons = vertical(
    button("Close") { showModal.value = false },
)
val modalComponent = renderer(modalButtons) {
    vbox(text("Modal"), modalButtons.render()).border()
}

val combined = mainComponent.modal(modalComponent, showModal)
app.loop(combined)
```

## resizableSplit

A split pane where the user can drag the separator to resize.

```kotlin
// Convenience variants:
val mainSize = IntState(20)
resizableSplitLeft(mainComp, backComp, mainSize)    // main on left
resizableSplitRight(mainComp, backComp, mainSize)   // main on right
resizableSplitTop(mainComp, backComp, mainSize)     // main on top
resizableSplitBottom(mainComp, backComp, mainSize)  // main on bottom
```

Full-options variant with custom separator and size constraints:

```kotlin
resizableSplit(
    main = leftPanel,
    back = rightPanel,
    mainSize = IntState(30),
    direction = Direction.Left,
    minSize = IntState(10),
    maxSize = IntState(60),
    separator = { separatorHeavy() },
)
```

Nesting splits to create four-pane layouts:

```kotlin
var container: Component = centerPanel
container = resizableSplitLeft(leftPanel, container, IntState(20))
container = resizableSplitRight(rightPanel, container, IntState(20))
container = resizableSplitTop(topPanel, container, IntState(10))
container = resizableSplitBottom(bottomPanel, container, IntState(5))
```

## poll

Calls `onPoll` every event-loop tick. Use for background work that needs to trigger re-renders.

```kotlin
val poller = poll(app) {
    // check a queue, update state, etc.
}
val layout = vertical(poller, mainComponent)
```

## Memory

- `CollapsibleComponent`, `maybe`, `modal`, and `resizableSplit` transfer ownership of their children — do not destroy children separately.
- `IntState`/`BoolState` passed to these components must be freed by the caller.
