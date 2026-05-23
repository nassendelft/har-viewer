package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.LessThan
import nl.ncaj.WidthOrHeight.Height

@OptIn(ExperimentalForeignApi::class)
fun exampleCheckboxInFrame() {
    val states = Array(30) { BoolState(false) }

    val container = vertical()
    for (i in 0 until 30) {
        container.add(checkbox("Checkbox$i", states[i]))
    }

    val rendererComp = renderer(container) {
        container.render()
            .vscrollIndicator()
            .frame()
            .size(Height, LessThan, 10)
            .border()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}