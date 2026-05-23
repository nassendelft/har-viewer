package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleRenderer() {
    val screen = FtxUIApp.fitComponent()

    // 1. Non-focusable renderer
    val rendererNonFocusable = renderer {
        text("~~~~~ Non Focusable renderer() ~~~~~")
    }

    // 2. Renderer wrapping a button component, coloring it red
    val quitButton = button("Wrapped quit button", screen.exitClosure())
    val rendererWrap = renderer(quitButton) {
        quitButton.render().color(Color.Red)
    }

    screen.loop(vertical(rendererNonFocusable, rendererWrap))
}