package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.exp

@OptIn(ExperimentalForeignApi::class)
fun exampleCanvasAnimated() {
    var mouseX = 0
    var mouseY = 0

    fun makeLineBrailleRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "Several lines (braille)")
        c.drawPointLine(mouseX, mouseY, 80, 10, Color.Red)
        c.drawPointLine(80, 10, 80, 40, Color.Blue)
        c.drawPointLine(80, 40, mouseX, mouseY, Color.Green)
        c.toElement()
    }

    fun makeLineBlockRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "Several lines (block)")
        c.drawBlockLine(mouseX, mouseY, 80, 10, Color.Red)
        c.drawBlockLine(80, 10, 80, 40, Color.Blue)
        c.drawBlockLine(80, 40, mouseX, mouseY, Color.Green)
        c.toElement()
    }

    fun makeCircleBrailleRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A circle (braille)")
        c.drawPointCircle(mouseX, mouseY, 30)
        c.toElement()
    }

    fun makeCircleBlockRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A circle (block)")
        c.drawBlockCircle(mouseX, mouseY, 30)
        c.toElement()
    }

    fun makeCircleFilledBrailleRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A circle filled (braille)")
        c.drawPointCircleFilled(mouseX, mouseY, 30)
        c.toElement()
    }

    fun makeCircleFilledBlockRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A circle filled (block)")
        c.drawBlockCircleFilled(mouseX, mouseY, 30)
        c.toElement()
    }

    fun makeEllipseBrailleRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "An ellipse (braille)")
        c.drawPointEllipse(mouseX / 2, mouseY / 2, mouseX / 2, mouseY / 2)
        c.toElement()
    }

    fun makeEllipseBlockRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "An ellipse (block)")
        c.drawBlockEllipse(mouseX / 2, mouseY / 2, mouseX / 2, mouseY / 2)
        c.toElement()
    }

    fun makeEllipseFilledBrailleRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A filled ellipse (braille)")
        c.drawPointEllipseFilled(mouseX / 2, mouseY / 2, mouseX / 2, mouseY / 2)
        c.toElement()
    }

    fun makeEllipseFilledBlockRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A filled ellipse (block)")
        c.drawBlockEllipseFilled(mouseX / 2, mouseY / 2, mouseX / 2, mouseY / 2)
        c.drawBlockEllipse(mouseX / 2, mouseY / 2, mouseX / 2, mouseY / 2)
        c.toElement()
    }

    fun makePlot1Renderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A graph")
        val ys = IntArray(100) { x ->
            val dx = (x - mouseX).toFloat()
            val dy = 50f
            (dy + 20 * cos(dx * 0.14f) + 10 * sin(dx * 0.42f)).toInt()
        }
        for (x in 1 until 99) c.drawPointLine(x, ys[x], x + 1, ys[x + 1])
        c.toElement()
    }

    fun makePlot2Renderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A symmetrical graph filled")
        val ys = IntArray(100) { x ->
            (30 + 10 * cos(x * 0.2 - mouseX * 0.05) +
                    5 * sin(x * 0.4) + 5 * sin(x * 0.3 - mouseY * 0.05)).toInt()
        }
        for (x in 0 until 100) c.drawPointLine(x, 50 + ys[x], x, 50 - ys[x], Color.Red)
        c.toElement()
    }

    fun makePlot3Renderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A 2D gaussian plot")
        val size = 15
        val my = (mouseY - 90) / -5f
        val mx = (mouseX - 3 * my) / 5f
        val ys = Array(size) { y -> FloatArray(size) { x ->
            val dx = x - mx; val dy = y - my
            -1.5f + 3.0f * exp(-0.2f * (dx * dx + dy * dy)).toFloat()
        }}
        for (y in 0 until size) for (x in 0 until size) {
            if (x != 0) c.drawPointLine(
                (5 * (x - 1) + 3 * y).toInt(), (90 - 5 * y - 5 * ys[y][x - 1]).toInt(),
                (5 * x + 3 * y).toInt(), (90 - 5 * y - 5 * ys[y][x]).toInt()
            )
            if (y != 0) c.drawPointLine(
                (5 * x + 3 * (y - 1)).toInt(), (90 - 5 * (y - 1) - 5 * ys[y - 1][x]).toInt(),
                (5 * x + 3 * y).toInt(), (90 - 5 * y - 5 * ys[y][x]).toInt()
            )
        }
        c.toElement()
    }

    fun makeTextRenderer() = renderer {
        val c = Canvas(100, 100)
        c.drawText(0, 0, "A piece of text")
        c.drawText(mouseX, mouseY, "This is a piece of text with effects", Color.Red)
        c.toElement()
    }

    val tabSelected = IntState(12)
    val tabTitles = listOf(
        "line (braille)", "line (block)",
        "circle (braille)", "circle (block)",
        "circle filled (braille)", "circle filled (block)",
        "ellipse (braille)", "ellipse (block)",
        "ellipse filled (braille)", "ellipse filled (block)",
        "plot_1 simple", "plot_2 filled", "plot_3 3D", "text",
    )

    val renderers = listOf(
        makeLineBrailleRenderer(), makeLineBlockRenderer(),
        makeCircleBrailleRenderer(), makeCircleBlockRenderer(),
        makeCircleFilledBrailleRenderer(), makeCircleFilledBlockRenderer(),
        makeEllipseBrailleRenderer(), makeEllipseBlockRenderer(),
        makeEllipseFilledBrailleRenderer(), makeEllipseFilledBlockRenderer(),
        makePlot1Renderer(), makePlot2Renderer(), makePlot3Renderer(),
        makeTextRenderer(),
    )

    val tabContainer = tab(tabSelected)
    for (r in renderers) tabContainer.add(r)

    val tabWithMouse = tabContainer.catchEvent { e ->
        if (e.isMouse) {
            mouseX = (e.mouseX - 1) * 2
            mouseY = (e.mouseY - 1) * 4
        }
        false
    }

    val tabToggle = menu(tabTitles, tabSelected)
    val layout = horizontal(tabWithMouse, tabToggle)

    val component = renderer(layout) {
        hbox(
            tabWithMouse.render(),
            separator(),
            tabToggle.render(),
        ).border()
    }

    val app = FtxUIApp.fitComponent()
    app.loop(component)
    app.destroy()
    component.destroy()
    tabSelected.free()
}