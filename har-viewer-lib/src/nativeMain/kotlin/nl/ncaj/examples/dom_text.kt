package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomText() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer {
        text(
            "FTXUI is a C++ library for terminal-based user interfaces.\n" +
            "It is functional, responsive, and cross-platform.\n" +
            "You can use newlines directly within a single text() element,\n" +
            "making it easier to display multi-line strings."
        ).border().center()
    })
}
