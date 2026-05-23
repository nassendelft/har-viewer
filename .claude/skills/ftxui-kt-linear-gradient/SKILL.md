---
name: ftxui-kt-linear-gradient
description: LinearGradient — define multi-stop color gradients for element foreground and background.
license: MIT
compatibility: opencode
---

`LinearGradient` defines a gradient with an angle and one or more color stops. Apply it to elements via `colorLinearGradient()` (foreground) or `bgcolorLinearGradient()` (background).

## Creating a gradient

```kotlin
val gradient = LinearGradient()
    .angle(45f)               // degrees; 0 = left→right, 90 = bottom→top
    .stop(Color.Red)          // evenly distributed stops
    .stop(Color.Yellow)
    .stop(Color.Green)
```

With explicit stop positions (0.0 to 1.0):

```kotlin
val gradient = LinearGradient()
    .stop(Color.Blue, 0.0f)
    .stop(Color.Cyan, 0.5f)
    .stop(Color.White, 1.0f)
```

## Applying to elements

```kotlin
text("Rainbow text").colorLinearGradient(gradient)
text("Gradient background").bgcolorLinearGradient(gradient)

// Both together:
filler()
    .bgcolorLinearGradient(gradient)
    .size(WidthOrHeight.Height, Constraint.Equal, 3)
```

## Full example

```kotlin
val grad = LinearGradient()
    .angle(0f)
    .stop(Color.Red)
    .stop(Color.Blue)

renderer {
    vbox(
        text("Gradient demo").bold().hcenter(),
        filler().bgcolorLinearGradient(grad).size(WidthOrHeight.Height, Constraint.Equal, 5),
        text("colored text").colorLinearGradient(grad),
    ).border()
}
```

## Memory

Destroy the gradient after use:

```kotlin
grad.destroy()
```

The gradient handle is copied into the element when `colorLinearGradient()` is called, so it's safe to destroy after the element has been constructed.
