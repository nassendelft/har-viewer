package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomBorderStyle() {
    val rendererComp = renderer {
        vbox(
            text("borderLight").borderLight(),
            text("borderDashed").borderDashed(),
            text("borderHeavy").borderHeavy(),
            text("borderDouble").borderDouble(),
            text("borderRounded").borderRounded(),
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
