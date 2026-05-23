package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleTabHorizontal() {
    val tabValues = listOf("tab_1", "tab_2", "tab_3")
    val tabSelected = IntState(0)
    val tabToggle = toggle(tabValues, tabSelected)

    val tab1Entries = listOf("Forest", "Water", "I don't know")
    val tab2Entries = listOf("Hello", "Hi", "Hay")
    val tab3Entries = listOf("Table", "Nothing", "Is", "Empty")

    val tabContainerSelected = IntState(0)
    val tabContainer = tab(tabContainerSelected)
    tabContainer.add(radiobox(tab1Entries, IntState(0)))
    tabContainer.add(radiobox(tab2Entries, IntState(0)))
    tabContainer.add(radiobox(tab3Entries, IntState(0)))

    val container = vertical(tabToggle, tabContainer)

    val rendererComp = renderer(container) {
        // Sync toggle selection into the tab container
        tabContainerSelected.value = tabSelected.value
        vbox(
            tabToggle.render(),
            separator(),
            tabContainer.render(),
        ).border()
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(rendererComp)
}