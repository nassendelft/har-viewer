package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMaybe() {
    val entries = listOf("entry 1", "entry 2", "entry 3")

    val menu1Selected = IntState(0)
    val menu2Selected = IntState(0)

    val menu1Show = BoolState(false)
    val menu2Show = BoolState(false)

    val menu1ShowCheckbox = checkbox("Show menu_1", menu1Show)
    val menu1Radiobox = radiobox(entries, menu1Selected).maybe(menu1Show)
    val menu2ShowCheckbox = checkbox("Show menu_2", menu2Show)
    val menu2Radiobox = radiobox(entries, menu2Selected).maybe(menu2Show)

    val layout = vertical(
        menu1ShowCheckbox,
        menu1Radiobox,
        menu2ShowCheckbox,
        menu2Radiobox,
    )

    val screen = FtxUIApp.terminalOutput()
    screen.loop(layout)
}