package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomSeparatorStyle() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer {
        vbox(
            vbox(
                text("separatorLight"),
                separatorLight(),
                hbox(text("left"), separatorLight(), text("right"))
            ).borderLight(),
            vbox(
                text("separatorDashed"),
                separatorDashed(),
                hbox(text("left"), separatorDashed(), text("right"))
            ).borderDashed(),
            vbox(
                text("separatorHeavy"),
                separatorHeavy(),
                hbox(text("left"), separatorHeavy(), text("right"))
            ).borderHeavy(),
            vbox(
                text("separatorDouble"),
                separatorDouble(),
                hbox(text("left"), separatorDouble(), text("right"))
            ).borderDouble()
        )
    })
}
