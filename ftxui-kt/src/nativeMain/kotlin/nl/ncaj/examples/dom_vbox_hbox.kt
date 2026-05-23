package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomVboxHbox() {
    val rendererComp = renderer {
        vbox(
            hbox(
                text("north-west"),
                filler(),
                text("north-east"),
            ),
            filler(),
            hbox(
                filler(),
                text("center"),
                filler(),
            ),
            filler(),
            hbox(
                text("south-west"),
                filler(),
                text("south-east"),
            ),
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
