package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.Equal
import nl.ncaj.WidthOrHeight.Width

@OptIn(ExperimentalForeignApi::class)
fun exampleDomSpinner() {
    var index = 0
    val screen = FtxUIApp.fitComponent()
    val rendererComp = renderer {
        val entries = mutableListOf<Element>()
        for (i in 0 until 23) {
            if (i != 0) entries.add(separator())
            entries.add(
                hbox(
                    text(i.toString()).size(Width, Equal, 2),
                    separator(),
                    spinner(i, index).bold(),
                )
            )
        }
        screen.requestAnimationFrame()
        index++
        hbox(
            vbox(*entries.toTypedArray()).border(),
            filler(),
        )
    }
    screen.loop(rendererComp)
}
