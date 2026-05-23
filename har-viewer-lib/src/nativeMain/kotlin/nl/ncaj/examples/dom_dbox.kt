package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomDbox() {
    val rendererComp = renderer {
        dbox(
            vbox(
                text("line_1"),
                text("line_2"),
                text("line_3"),
                text("line_4"),
                text("line_5"),
            ).border(),
            text("overlay").border().center(),
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
