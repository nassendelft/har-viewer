package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleResizableSplit() {
    val leftSize = IntState(20)
    val rightSize = IntState(20)
    val topSize = IntState(10)
    val bottomSize = IntState(10)

    val middle = renderer { text("Middle").center() }
    val left = renderer { text("Left: ${leftSize.value}").center() }
    val right = renderer { text("Right: ${rightSize.value}").center() }
    val top = renderer { text("Top: ${topSize.value}").center() }
    val bottom = renderer { text("Bottom: ${bottomSize.value}").center() }

    var container: Component = middle
    container = resizableSplitLeft(left, container, leftSize)
    container = resizableSplitRight(right, container, rightSize)
    container = resizableSplitTop(top, container, topSize)
    container = resizableSplitBottom(bottom, container, bottomSize)

    val finalContainer = container
    val rendererComp = renderer(finalContainer) {
        finalContainer.render().border()
    }

    val screen = FtxUIApp.fullscreen()
    screen.loop(rendererComp)
}