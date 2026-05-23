package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleInputInFrame() {
    val items = List(20) { i -> StringState() to "placeholder $i" }
    val inputList = vertical(*Array(items.size) { i -> input(items[i].first, items[i].second) })

    val r = renderer(inputList) {
        inputList.render()
            .vscrollIndicator()
            .frame()
            .border()
            .size(WidthOrHeight.Height, Constraint.LessThan, 10)
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(r)
}