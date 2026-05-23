package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMenu() {
    val entries = listOf("entry 1", "entry 2", "entry 3")
    val selected = IntState(0)
    val menuComponent = menu(entries, selected)

    val component = renderer(menuComponent) {
        vbox(
            menuComponent.render(),
            separator(),
            text("Selected: ${entries[selected.value]}")
        )
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(component)
    selected.free()
}