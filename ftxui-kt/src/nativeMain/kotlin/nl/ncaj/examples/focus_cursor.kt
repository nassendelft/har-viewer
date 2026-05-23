package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleFocusCursor() {
    fun instance(label: String, focusCursor: (Element) -> Element): Component =
        focusableRenderer { focused ->
            if (focused) {
                hbox(text("> $label "), focusCursor(text(" ")))
            } else {
                text("  $label ")
            }
        }

    val container = vertical(
        instance("focus") { it.focus() },
        instance("focusCursorBlock") { it.focusCursorBlock() },
        instance("focusCursorBlockBlinking") { it.focusCursorBlockBlinking() },
        instance("focusCursorBar") { it.focusCursorBar() },
        instance("focusCursorBarBlinking") { it.focusCursorBarBlinking() },
        instance("focusCursorUnderline") { it.focusCursorUnderline() },
        instance("focusCursorUnderlineBlinking") { it.focusCursorUnderlineBlinking() },
    )

    val screen = FtxUIApp.fullscreen()
    screen.loop(container)
}