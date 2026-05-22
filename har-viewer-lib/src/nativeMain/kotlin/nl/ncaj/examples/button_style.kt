package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleButtonStyle() {
    var value = 0
    val action: () -> Unit = { value++ }
    val actionRenderer = renderer { text("count = $value") }

    val buttons = vertical(
        actionRenderer,
        renderer { separator() },
        horizontal(
            vertical(
                button("Ascii 1", action, ButtonOption.ascii()),
                button("Ascii 2", action, ButtonOption.ascii()),
                button("Ascii 3", action, ButtonOption.ascii()),
            ),
            renderer { separator() },
            vertical(
                button("Simple 1", action, ButtonOption.simple()),
                button("Simple 2", action, ButtonOption.simple()),
                button("Simple 3", action, ButtonOption.simple()),
            ),
            renderer { separator() },
            vertical(
                button("Animated 1", action, ButtonOption.animated(Color.Red)),
                button("Animated 2", action, ButtonOption.animated(Color.Green)),
                button("Animated 3", action, ButtonOption.animated(Color.Blue)),
            )
        )
    )

    val screen = FtxUIApp.fitComponent()
    screen.loop(buttons)
}