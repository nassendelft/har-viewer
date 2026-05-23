package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomGaugeDirection() {
    val percentage = 0.7f

    val rendererComp = renderer {
        val gaugeUp = hbox(
            vtext("gauge vertical"),
            separator(),
            gaugeUp(percentage),
        ).border()

        val gaugeDown = hbox(
            vtext("gauge vertical"),
            separator(),
            gaugeDown(percentage),
        ).border()

        val gaugeRight = vbox(
            text("gauge horizontal"),
            separator(),
            gaugeRight(percentage),
        ).border()

        val gaugeLeft = vbox(
            text("gauge horizontal"),
            separator(),
            gaugeLeft(percentage),
        ).border()

        hbox(
            gaugeUp,
            filler(),
            vbox(
                gaugeRight,
                filler(),
                text("${(percentage * 5000).toInt()}/5000").border().center(),
                filler(),
                gaugeLeft,
            ),
            filler(),
            gaugeDown,
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
