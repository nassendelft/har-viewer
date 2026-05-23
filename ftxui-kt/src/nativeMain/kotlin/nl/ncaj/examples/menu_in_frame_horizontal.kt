package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMenuInFrameHorizontal() {
    val entries = List(100) { i -> i.toString() }
    val selected = IntState(0)
    val menu = menuHorizontal(entries, selected)

    val r = menu.decorateRender { inner ->
        inner.hscrollIndicator().frame()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(r)
}