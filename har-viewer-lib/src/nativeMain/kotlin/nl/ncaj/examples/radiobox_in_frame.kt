package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.LessThan
import nl.ncaj.WidthOrHeight.Height

@OptIn(ExperimentalForeignApi::class)
fun exampleRadioboxInFrame() {
    val entries = (0 until 30).map { "RadioBox $it" }
    val selected = IntState(0)
    val radioboxComponent = radiobox(entries, selected)

    val component = renderer(radioboxComponent) {
        radioboxComponent.render()
            .vscrollIndicator()
            .frame()
            .size(Height, LessThan, 10)
            .border()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(component)
    selected.free()
}