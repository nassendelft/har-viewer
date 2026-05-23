package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMenuMultiple() {
    val menuEntries = listOf(
        listOf("Ananas", "Raspberry", "Citrus"),
        listOf("Potatoes", "Weat", "Rise"),
        listOf("Carrot", "Lettuce", "Tomato"),
    )

    val menuSelected = Array(3) { IntState(0) }
    var menuSelectedGlobal = 0

    fun windowComp(title: String, child: Component): Component =
        renderer(child) { child.render().window(text(title)).flex() }

    val menuGlobal = vertical(
        windowComp("Menu 1", menu(menuEntries[0], menuSelected[0])),
        windowComp("Menu 2", menu(menuEntries[1], menuSelected[1])),
        windowComp("Menu 3", menu(menuEntries[2], menuSelected[2])),
    )

    val info = renderer {
        val g = menuSelectedGlobal
        val value = menuEntries[g][menuSelected[g].value]
        vbox(
            text("menu_selected_global = $g"),
            text("menu_selected[0]     = ${menuSelected[0].value}"),
            text("menu_selected[1]     = ${menuSelected[1].value}"),
            text("menu_selected[2]     = ${menuSelected[2].value}"),
            text("Value                = $value"),
        ).window(text("Content")).flex()
    }

    val global = horizontal(menuGlobal, info)

    val rendererComp = renderer(global) {
        // Track which menu column is focused via menuSelectedGlobal
        menuSelectedGlobal = 0 // default; FTXUI doesn't expose global focus directly
        global.render()
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(rendererComp)
}