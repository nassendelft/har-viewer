---
name: ftxui-kt-renderer
description: renderer(), focusableRenderer(), decorateRender(), and catchEvent() — custom rendering and event handling.
license: MIT
compatibility: opencode
---

## renderer

Wraps a callback that produces an `Element` on every frame. Optionally takes a `child` component whose state is updated before rendering.

```kotlin
fun renderer(child: Component? = null, callback: () -> Element): Component
```

Without a child (pure display):

```kotlin
val display = renderer {
    vbox(
        text("Frame: $frameCount"),
        gauge(progress),
    ).border()
}
```

With a child (child processes events; renderer controls what's shown):

```kotlin
val inputComp = input(nameState, "name")
val r = renderer(inputComp) {
    vbox(
        hbox(text("Name: "), inputComp.render()),
        text("Hello, ${nameState.value}"),
    ).border()
}
```

Passing a child is the standard way to compose interactive components into a custom layout. The child gets events; the renderer decides how to draw everything.

## focusableRenderer

Like `renderer` but receives a `focused` parameter, allowing the component to change its appearance based on keyboard focus:

```kotlin
val comp = focusableRenderer { focused ->
    text("Press Enter")
        .let { if (focused) it.inverted() else it }
        .border()
}
```

## Component.render()

Render a component to an `Element` for use inside a `renderer` callback:

```kotlin
val elem: Element = myComponent.render()
```

This is how you embed an interactive component's visual output in a custom layout.

## Component.decorateRender

Wraps the component's render output with a transform, without replacing the render entirely. Transfers ownership.

```kotlin
val decorated = myComponent.decorateRender { inner ->
    inner.borderRounded().color(Color.Cyan)
}
```

## catchEvent

Intercept keyboard and mouse events. Return `true` to consume (stop propagation), `false` to let the event continue down the component tree.

```kotlin
fun Component.catchEvent(handler: (FtxUIEvent) -> Boolean): Component
```

```kotlin
val withEsc = myComponent.catchEvent { event ->
    when {
        event.character == "q" -> { app.exit(); true }
        event.input == "\x1B" -> { handleEscape(); true }
        event.isMouse -> {
            mouseX = event.mouseX
            mouseY = event.mouseY
            false  // don't consume, let child handle too
        }
        else -> false
    }
}
```

`FtxUIEvent` fields:
- `input: String` — raw escape sequence
- `debugString: String`
- `isCharacter: Boolean`
- `character: String` — single char if `isCharacter`
- `isMouse: Boolean`
- `mouseX: Int`, `mouseY: Int` — terminal coordinates (1-indexed)

## Ownership rules

- `renderer(child, callback)` — takes ownership of `child`. Destroy only the returned component.
- `decorateRender` — takes ownership of the source component.
- `catchEvent` — takes ownership of the source component.
- Chaining multiple decorators: each step transfers ownership, so only call `destroy()` on the final result.

```kotlin
val final = myComp
    .catchEvent { e -> handleKey(e) }
    .decorateRender { it.borderRounded() }
// Only destroy `final`, not `myComp`.
final.destroy()
```
