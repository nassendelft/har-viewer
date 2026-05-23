package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import kotlin.math.sin

@OptIn(ExperimentalForeignApi::class)
fun exampleSliderDirection() {
    val values = List(30) { i -> IntState((50 + (20 * sin(i * 0.3))).toInt()) }
    val sliders = values.map { v -> slider(v, 0, 100, 5, Direction.Up) }

    val layout: Component = horizontal(*sliders.toTypedArray())
        .size(WidthOrHeight.Height, Constraint.GreaterThan, 20)

    val screen = FtxUIApp.terminalOutput()
    screen.loop(layout)
}