package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomStyleUnderlinedDouble() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer { hbox(text("This text is "), text("underlined double").underlinedDouble(), text(". Do you like it?")) })
}
