package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleToggle() {
    val toggle1Entries = listOf("On", "Off")
    val toggle2Entries = listOf("Enabled", "Disabled")
    val toggle3Entries = listOf("10€", "0€")
    val toggle4Entries = listOf("Nothing", "One element", "Several elements")

    val toggle1Selected = IntState(0)
    val toggle2Selected = IntState(0)
    val toggle3Selected = IntState(0)
    val toggle4Selected = IntState(0)

    val toggle1 = toggle(toggle1Entries, toggle1Selected)
    val toggle2 = toggle(toggle2Entries, toggle2Selected)
    val toggle3 = toggle(toggle3Entries, toggle3Selected)
    val toggle4 = toggle(toggle4Entries, toggle4Selected)

    val container = vertical(toggle1, toggle2, toggle3, toggle4)

    val rendererComp = renderer(container) {
        vbox(
            text("Choose your options:"),
            text(""),
            hbox(text(" * Poweroff on startup      : "), toggle1.render()),
            hbox(text(" * Out of process           : "), toggle2.render()),
            hbox(text(" * Price of the information : "), toggle3.render()),
            hbox(text(" * Number of elements       : "), toggle4.render()),
        )
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(rendererComp)
}