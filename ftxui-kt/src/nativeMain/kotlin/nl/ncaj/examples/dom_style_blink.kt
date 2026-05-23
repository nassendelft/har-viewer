package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomStyleBlink() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer { hbox(text("This text is "), text("blinking").blink(), text(". Do you like it?")) })
}