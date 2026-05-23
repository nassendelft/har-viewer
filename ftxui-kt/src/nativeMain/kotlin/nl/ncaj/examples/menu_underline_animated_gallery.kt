package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMenuUnderlineAnimatedGallery() {
    val tabValues = listOf("Tab 1", "Tab 2", "Tab 3", "A very very long tab", "Tab")
    val tabSelected = IntState(0)

    val menu1 = menuHorizontalAnimated(tabValues, tabSelected)
    val menu2 = menuHorizontalAnimated(tabValues, tabSelected)
    val menu3 = menuHorizontalAnimated(tabValues, tabSelected)

    val layout = vertical(menu1, menu2, menu3)

    val component = renderer(layout) {
        vbox(
            text("Default HorizontalAnimated menu:"),
            menu1.render(),
            separator(),
            text("Second instance (shared selected state):"),
            menu2.render(),
            separator(),
            text("Third instance:"),
            menu3.render(),
            separator(),
            text("Selected: ${tabValues[tabSelected.value]}"),
        ).border()
    }

    val app = FtxUIApp.fitComponent()
    app.loop(component)
    app.destroy()
    component.destroy()
    tabSelected.free()
}