package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleScrollbar() {
    val scrollX = FloatState(0.1f)
    val scrollY = FloatState(0.1f)

    val lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed " +
        "do eiusmod tempor incididunt ut labore et dolore magna " +
        "aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
        "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis " +
        "aute irure dolor in reprehenderit in voluptate velit esse " +
        "cillum dolore eu fugiat nulla pariatur. Excepteur sint " +
        "occaecat cupidatat non proident, sunt in culpa qui officia " +
        "deserunt mollit anim id est laborum."

    val content = renderer {
        vbox(
            *Array(15) { i -> text(lorem.substring(minOf(i * 10, lorem.length))) }
        )
    }

    val scrollableContent = content.decorateRender { inner ->
        inner.focusPositionRelative(scrollX.value, scrollY.value).frame().flex()
    }

    val scrollbarX = slider(scrollX, 0f, 1f, 0.1f, Direction.Right, Color.Blue, Color.BlueLight)
    val scrollbarY = slider(scrollY, 0f, 1f, 0.1f, Direction.Down, Color.Yellow, Color.YellowLight)
    val cornerText = renderer { text("x") }

    val layout = vertical(
        horizontal(scrollableContent, scrollbarY).flex(),
        horizontal(scrollbarX, cornerText),
    )

    val screen = FtxUIApp.fitComponent()
    screen.loop(layout)
}