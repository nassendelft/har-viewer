package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.LessThan
import nl.ncaj.WidthOrHeight.Height

@OptIn(ExperimentalForeignApi::class)
fun exampleMenuInFrame() {
    val entries = (0 until 30).map { "Entry $it" }
    val selected = IntState(0)
    val menuComp = menu(entries, selected)

    val rendererComp = renderer(menuComp) {
        menuComp.render()
            .vscrollIndicator()
            .frame()
            .size(Height, LessThan, 10)
            .border()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}