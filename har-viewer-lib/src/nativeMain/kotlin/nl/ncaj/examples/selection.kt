package nl.ncaj.examples

import ftxui_c.ftxui_cell_style_callback_t
import kotlinx.cinterop.*
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
private val underlinedDoubleStyle: ftxui_cell_style_callback_t = staticCFunction { cell, _ ->
    cell?.pointed?.apply { underlined_double = true }
}

@OptIn(ExperimentalForeignApi::class)
fun exampleSelection() {
    val app = FtxUIApp.terminalOutput()

    var selectionChangeCounter = 0
    var selectionContent = ""

    app.selectionChange {
        selectionChangeCounter++
        selectionContent = app.getSelection()
    }

    fun loremIpsum() = text(
        "FTXUI: A powerful library for building user interfaces. " +
        "Enjoy a rich set of components and a declarative style. " +
        "Create beautiful and responsive UIs with minimal effort. " +
        "Join the community and experience the power of FTXUI."
    )

    val quitButton = button("Quit", { app.exit() }, ButtonOption.animated())

    val mainRenderer = renderer(quitButton) {
        vbox(
            text("Selection changed: $selectionChangeCounter times"),
            text("Currently selected:"),
            paragraph(selectionContent).vscrollIndicator().frame().border()
                .size(WidthOrHeight.Height, Constraint.Equal, 10),
            hbox(
                loremIpsum(),
                separator(),
                loremIpsum(),
                separator(),
                loremIpsum(),
            ).window(text("Horizontal split")),
            vbox(
                loremIpsum(),
                separator(),
                loremIpsum(),
                separator(),
                loremIpsum(),
            ).window(text("Vertical split")),
            vbox(
                hbox(
                    loremIpsum(),
                    separator(),
                    loremIpsum().selectionBgColor(Color.Yellow).selectionFgColor(Color.Black).selectionStyleReset(),
                    separator(),
                    loremIpsum().selectionColor(Color.Blue),
                ),
                separator(),
                hbox(
                    loremIpsum().selectionColor(Color.Red),
                    separator(),
                    loremIpsum().selectionStyle(underlinedDoubleStyle),
                    separator(),
                    loremIpsum(),
                ),
            ).window(text("Grid split with different style")),
            quitButton.render(),
        )
    }

    app.loop(mainRenderer)
    app.destroy()
}