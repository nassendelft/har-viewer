package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.Equal
import nl.ncaj.WidthOrHeight.Width

@OptIn(ExperimentalForeignApi::class)
fun exampleDomSize() {
    fun makeBox(title: String): Element =
        text("content").hcenter().dim()
            .window(text(title).hcenter().bold())

    val rendererComp = renderer {
        val elements = (3 until 30).map { x ->
            makeBox(x.toString()).size(Width, Equal, x)
        }
        hbox(*elements.toTypedArray())
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
