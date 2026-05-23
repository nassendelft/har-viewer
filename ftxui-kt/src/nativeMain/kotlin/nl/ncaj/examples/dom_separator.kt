package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomSeparator() {
    val rendererComp = renderer {
        hbox(
            text("left-column"),
            separator(),
            vbox(
                text("top").hcenter().flex(),
                separator(),
                text("bottom").hcenter(),
            ).flex(),
            separator(),
            text("right-column"),
        ).border()
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
