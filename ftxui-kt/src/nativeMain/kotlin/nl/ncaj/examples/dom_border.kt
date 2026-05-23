package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomBorder() {
    val rendererComp = renderer {
        hbox(
            vbox(
                text("Line 1"),
                text("Line 2"),
                text("Line 3"),
            ).border(),
            vbox(
                text("Line 4"),
                text("Line 5"),
                text("Line 6"),
            ).border(),
            vbox(
                text("Line 7"),
                text("Line 8"),
                text("Line 9"),
            ).border(),
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}