package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomColorTruecolorRgb() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer {
        val redLine = mutableListOf<Element>()
        val greenLine = mutableListOf<Element>()
        val blueLine = mutableListOf<Element>()
        val cyanLine = mutableListOf<Element>()
        val magentaLine = mutableListOf<Element>()
        val yellowLine = mutableListOf<Element>()

        for (value in 0 until 255 step 3) {
            val v = (value * value / 255).toUByte()
            val z: UByte = 0u
            redLine.add(text(" ").bgcolor(Color.rgb(v, z, z)))
            greenLine.add(text(" ").bgcolor(Color.rgb(z, v, z)))
            blueLine.add(text(" ").bgcolor(Color.rgb(z, z, v)))
            cyanLine.add(text(" ").bgcolor(Color.rgb(z, v, v)))
            magentaLine.add(text(" ").bgcolor(Color.rgb(v, z, v)))
            yellowLine.add(text(" ").bgcolor(Color.rgb(v, v, z)))
        }

        vbox(
            vbox(
                hbox(text("Red line    :"), hbox(*redLine.toTypedArray())),
                hbox(text("Green line  :"), hbox(*greenLine.toTypedArray())),
                hbox(text("Blue line   :"), hbox(*blueLine.toTypedArray()))
            ).window(text("Primary colors")),
            vbox(
                hbox(text("cyan line   :"), hbox(*cyanLine.toTypedArray())),
                hbox(text("magenta line:"), hbox(*magentaLine.toTypedArray())),
                hbox(text("Yellow line :"), hbox(*yellowLine.toTypedArray()))
            ).window(text("Secondary colors"))
        )
    })
}
