package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomStyleBold() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer { hbox(text("This text is "), text("bold").bold(), text(". Do you like it?")) })
}
