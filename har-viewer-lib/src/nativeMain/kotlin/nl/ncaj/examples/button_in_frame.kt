package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.LessThan
import nl.ncaj.WidthOrHeight.Height

@OptIn(ExperimentalForeignApi::class)
fun exampleButtonInFrame() {
    var counter = 0
    val onClick: () -> Unit = { counter++ }

    val style = ButtonOption.animated(
        Color.Default,
        Color.GrayDark,
        Color.Default,
        Color.White
    )

    val container = vertical()
    for (i in 0 until 30) {
        val button = button("Button ${i + 1}", onClick, style)
        container.add(button)
    }

    val renderer = renderer(container) {
        vbox(
            hbox(
                text("Counter:"),
                text(counter.toString())
            ),
            separator(),
            container.render()
                .vscrollIndicator()
                .frame()
                .size(Height, LessThan, 20)
        ).border()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer)
}