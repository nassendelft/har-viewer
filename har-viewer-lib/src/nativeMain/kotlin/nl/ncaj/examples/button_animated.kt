package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleButtonAnimated() {
    var value = 50

    val buttons = horizontal(
        button(
            label = "Decrease",
            onClick = { value-- },
            options = ButtonOption.animated(Color.Red),
        ),
        button(
            label = "Reset",
            onClick = { value = 50 },
            options = ButtonOption.animated(Color.Green),
        ),
        button(
            label = "Increase",
            onClick = { value++ },
            options = ButtonOption.animated(Color.Blue),
        )
    )

    val component = renderer(buttons) {
        vbox(
            vbox(
                text("value = $value"),
                separator(),
                gauge(value * 0.01f)
            ).border(),
            buttons.render()
        )
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(component)
}
