package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomColorTruecolorHsv() {
    val saturation: UByte = 255u
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer {
        val rows = mutableListOf<Element>()
        for (value in 0 until 255 step 20) {
            val cells = mutableListOf<Element>()
            for (hue in 0 until 255 step 2) {
                cells.add(
                    text("▀")
                        .color(Color.hsv(hue.toUByte(), saturation, value.toUByte()))
                        .bgcolor(Color.hsv(hue.toUByte(), saturation, (value + 10).toUByte()))
                )
            }
            rows.add(hbox(*cells.toTypedArray()))
        }
        vbox(*rows.toTypedArray())
    })
}
