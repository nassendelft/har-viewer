package nl.ncaj.examples

import kotlinx.cinterop.*
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
private fun style(): ButtonOption {
    val options = ButtonOption.animated()
    options.transform = { s ->
        var element = text(s.label)
        if (s.focused) {
            element = element.bold()
        }
        element.center().borderEmpty().flex()
    }
    return options
}

@OptIn(ExperimentalForeignApi::class)
fun exampleButton() {
    var value = 50

    val btnDec01 = button("-1", onClick = { value -= 1 }, options = style())
    val btnInc01 = button("+1", onClick = { value += 1 }, options = style())
    val btnDec10 = button("-10", onClick = { value -= 10 }, options = style())
    val btnInc10 = button("+10", onClick = { value += 10 }, options = style())

    val buttons = vertical(
        horizontal(btnDec01, btnInc01),
        horizontal(btnDec10, btnInc10)
    )

    val component = renderer(buttons) {
        vbox(
            text("value = $value"),
            separator(),
            buttons.render().flex()
        ).flex().border()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(component)
}