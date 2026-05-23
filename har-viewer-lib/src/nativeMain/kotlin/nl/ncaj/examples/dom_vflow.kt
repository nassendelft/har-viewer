package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.Equal
import nl.ncaj.WidthOrHeight.Height
import nl.ncaj.WidthOrHeight.Width

@OptIn(ExperimentalForeignApi::class)
fun exampleDomVflow() {
    fun makeBox(dimx: Int, dimy: Int): Element {
        val title = "${dimx}x${dimy}"
        return text("content").hcenter().dim()
            .window(text(title).hcenter().bold())
            .size(Width, Equal, dimx)
            .size(Height, Equal, dimy)
    }

    val rendererComp = renderer {
        vflow(
            makeBox(7, 7),
            makeBox(7, 5),
            makeBox(5, 7),
            makeBox(10, 4),
            makeBox(10, 4),
            makeBox(10, 4),
            makeBox(10, 4),
            makeBox(11, 4),
            makeBox(11, 4),
            makeBox(11, 4),
            makeBox(11, 4),
            makeBox(12, 4),
            makeBox(12, 5),
            makeBox(12, 4),
            makeBox(13, 4),
            makeBox(13, 3),
            makeBox(13, 3),
            makeBox(10, 3),
        ).border()
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
