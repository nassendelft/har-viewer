package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleTextarea() {
    val content1 = StringState()
    val content2 = StringState()
    val size = IntState(50)

    val textarea1 = input(content1)
    val textarea2 = input(content2)

    val layout = resizableSplitLeft(textarea1, textarea2, size)
    val component = renderer(layout) {
        vbox(
            text("Input:"),
            separator(),
            layout.render().flex(),
        ).border()
    }

    val screen = FtxUIApp.fullscreen()
    screen.loop(component)
    content1.free()
    content2.free()
    size.free()
}