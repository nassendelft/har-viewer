package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomGauge() {
    fun gaugeRow(percentage: Float): Element {
        val data = "${(percentage * 5000).toInt()}/5000"
        return hbox(
            text("downloading:"),
            gauge(percentage).flex(),
            text(" $data"),
        )
    }

    val rendererComp = renderer {
        vbox(
            gaugeRow(0.0f),
            gaugeRow(0.25f),
            gaugeRow(0.5f),
            gaugeRow(0.75f),
            gaugeRow(1.0f),
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
