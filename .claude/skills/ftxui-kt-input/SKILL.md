---
name: ftxui-kt-input
description: input() and inputPassword() components for single-line text entry, with event-based validation.
license: MIT
compatibility: opencode
---

## Signatures

```kotlin
fun input(content: StringState, placeholder: String = ""): Component
fun inputPassword(content: StringState, placeholder: String = ""): Component
```

`StringState` holds the live text value. `inputPassword` masks the characters with `*`.

## Basic example

```kotlin
val name = StringState()
val nameInput = input(name, "enter name")

val screen = FtxUIApp.terminalOutput()
val component = renderer(nameInput) {
    vbox(
        hbox(text("Name: "), nameInput.render()),
        text("Hello, ${name.value}"),
    ).border()
}
screen.loop(component)
```

## Multiple inputs

Group inputs in a `vertical` container so Tab moves focus between them:

```kotlin
val firstName = StringState()
val lastName = StringState()
val password = StringState()

val inputs = vertical(
    input(firstName, "first name"),
    input(lastName, "last name"),
    inputPassword(password, "password"),
)

val r = renderer(inputs) {
    vbox(
        hbox(text("First : "), inputs.render()),
        // or render each child individually
    ).border()
}
```

## Input validation via catchEvent

`catchEvent` on an input component intercepts keystrokes before they reach the field. Return `true` to block the character.

```kotlin
val phoneNumber = StringState()
var phoneInput = input(phoneNumber, "phone number")

// Block non-digits:
phoneInput = phoneInput.catchEvent { event ->
    event.isCharacter && !event.character.first().isDigit()
}
// Enforce max length:
phoneInput = phoneInput.catchEvent { event ->
    event.isCharacter && phoneNumber.value.length >= 10
}
```

## Reading the value

Access `StringState.value` from anywhere — inside or outside the renderer:

```kotlin
val searchQuery = StringState()
val searchInput = input(searchQuery, "search...")

val submitBtn = button("Search") {
    performSearch(searchQuery.value)
}
```

## Memory

Call `StringState.free()` after destroying the associated component. The component does not free the state automatically.

```kotlin
component.destroy()
firstName.free()
```
