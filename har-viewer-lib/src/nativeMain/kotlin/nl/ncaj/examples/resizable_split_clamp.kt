package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleResizableSplitClamp() {
    val size = IntState(40)
    val sizeMin = IntState(10)
    val sizeMax = IntState(80)

    val left = renderer { text("Left").center() }
    val right = renderer { text("Right").center() }

    val split = resizableSplit(left, right, size, Direction.Left, sizeMin, sizeMax)

    val r = renderer(split) {
        vbox(
            text("Min:  ${sizeMin.value}"),
            text("Max:  ${sizeMax.value}"),
            text("Size: ${size.value}"),
            separator(),
            split.render().flex(),
        ).window(text("Drag the separator with the mouse"))
    }

    val screen = FtxUIApp.fullscreen()
    screen.loop(r)
}