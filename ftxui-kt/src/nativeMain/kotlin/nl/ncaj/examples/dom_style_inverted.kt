package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomStyleInverted() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer { hbox(text("This text is "), text("inverted").inverted(), text(". Do you like it?")) })
}
