package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleFocus() {
    val focusX = FloatState(0.5f)
    val focusY = FloatState(0.5f)

    val sliderX = slider("x", focusX, 0f, 1f, 0.01f)
    val sliderY = slider("y", focusY, 0f, 1f, 0.01f)

    fun makeBox(x: Int, y: Int): Element {
        val title = "($x, $y)"
        return text(title)
            .center()
            .size(WidthOrHeight.Width, Constraint.Equal, 18)
            .size(WidthOrHeight.Height, Constraint.Equal, 9)
            .border()
            .bgcolor(Color.hsv(
                (x * 255 / 15).toUByte(),
                255u,
                (y * 255 / 15).toUByte()
            ))
    }

    fun makeGrid(): Element {
        val rows = List(15) { i ->
            List(15) { j -> makeBox(i, j) }
        }
        return gridbox(rows)
    }

    val sliders = vertical(sliderX, sliderY)

    val r = renderer(sliders) {
        val title = "focusPositionRelative(${focusX.value}, ${focusY.value})"
        vbox(
            text(title),
            separator(),
            sliderX.render(),
            sliderY.render(),
            separator(),
            makeGrid().focusPositionRelative(focusX.value, focusY.value).frame().flex(),
        ).border()
    }

    val screen = FtxUIApp.fullscreen()
    screen.loop(r)
}