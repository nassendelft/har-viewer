---
name: ftxui-kt-button
description: button() component and ButtonOption — creating clickable buttons with various styles and custom transforms.
license: MIT
compatibility: opencode
---

## Signature

```kotlin
fun button(
    label: String,
    onClick: () -> Unit,
    options: ButtonOption = ButtonOption.simple()
): Component
```

## Preset styles

```kotlin
ButtonOption.simple()      // plain text, no decoration
ButtonOption.ascii()       // ASCII art border style
ButtonOption.border()      // Unicode border
ButtonOption.animated()    // animated color transition (default black/white palette)
ButtonOption.animated(Color.Blue)                              // single accent color
ButtonOption.animated(background, foreground)                  // explicit normal state
ButtonOption.animated(background, foreground, bgActive, fgActive)  // full four-color
```

## Basic example

```kotlin
var count = 0
val btn = button("+1", onClick = { count++ })

val screen = FtxUIApp.fitComponent()
screen.loop(renderer(btn) {
    vbox(text("Count: $count"), btn.render())
})
```

## Custom transform

Set `options.transform` to a `(EntryState) -> Element` lambda for pixel-level control over how the button looks in each state.

`EntryState` fields:
- `label: String` — the button label
- `state: Boolean` — currently pressed/active
- `active: Boolean` — mouse is over the button
- `focused: Boolean` — keyboard focus
- `index: Int` — position among siblings

```kotlin
val options = ButtonOption.animated()
options.transform = { s ->
    var element = text(s.label)
    if (s.focused) element = element.bold()
    element.center().borderEmpty().flex()
}
val btn = button("Click me", { doSomething() }, options)
```

## Quit button pattern

```kotlin
val app = FtxUIApp.fitComponent()
val quitBtn = button("Quit", app.exitClosure(), ButtonOption.border())
```

## Layout with multiple buttons

```kotlin
val dec = button("-1", { value-- }, ButtonOption.animated())
val inc = button("+1", { value++ }, ButtonOption.animated())

val layout = horizontal(dec, inc)
val component = renderer(layout) {
    hbox(dec.render(), inc.render()).border()
}
```

## Memory

`button()` owns the callback stableRef. Call `component.destroy()` to free it.
