package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import kotlin.math.cos

@OptIn(ExperimentalForeignApi::class)
fun exampleDomCanvas() {
    val c = Canvas(100, 100)

    c.drawText(0, 0, "This is a canvas", Color.Red)

    c.drawPointLine(10, 10, 80, 10, Color.Red)
    c.drawPointLine(80, 10, 80, 40, Color.Blue)
    c.drawPointLine(80, 40, 10, 10, Color.Green)

    c.drawPointCircle(30, 50, 20)
    c.drawPointCircleFilled(40, 40, 10)

    val ys = IntArray(100) { x -> (80 + 20 * cos(x * 0.2)).toInt() }
    for (x in 0 until 99) {
        c.drawPointLine(x, ys[x], x + 1, ys[x + 1], Color.Red)
    }

    val app = FtxUIApp.fitComponent()
    val component = renderer {
        c.toElement().border()
    }

    app.loop(component)
    app.destroy()
    component.destroy()
    c.destroy()
}