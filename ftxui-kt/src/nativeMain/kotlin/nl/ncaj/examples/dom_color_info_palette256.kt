package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomColorInfoPalette256() {
    val infoColumns = colorInfoSorted2D()

    val app = FtxUIApp.fitComponent()
    val component = renderer {
        val columnElements = infoColumns.map { column ->
            val entries = column.map { info ->
                val color = Color.palette256(info.index256)
                val el = hbox(
                    text("     ").bgcolor(color),
                    text(info.name),
                )
                color.destroy()
                el
            }
            vbox(*entries.toTypedArray())
        }
        hbox(*columnElements.toTypedArray())
    }

    app.loop(component)
    app.destroy()
    component.destroy()
}
