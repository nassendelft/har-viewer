package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleTabVertical() {
    val tabValues = listOf("tab_1", "tab_2", "tab_3")
    val tabSelected = IntState(0)
    val tabMenu = menu(tabValues, tabSelected)

    val tab1Entries = listOf("Forest", "Water", "I don't know")
    val tab1Selected = IntState(0)

    val tab2Entries = listOf("Hello", "Hi", "Hay")
    val tab2Selected = IntState(0)

    val tab3Entries = listOf("Table", "Nothing", "Is", "Empty")
    val tab3Selected = IntState(0)

    val tabContainer = tab(tabSelected)
    tabContainer.add(radiobox(tab1Entries, tab1Selected))
    tabContainer.add(radiobox(tab2Entries, tab2Selected))
    tabContainer.add(radiobox(tab3Entries, tab3Selected))

    val container = horizontal(tabMenu, tabContainer)

    val component = renderer(container) {
        hbox(
            tabMenu.render(),
            separator(),
            tabContainer.render()
        ).border()
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(component)
    tabSelected.free()
    tab1Selected.free()
    tab2Selected.free()
    tab3Selected.free()
}