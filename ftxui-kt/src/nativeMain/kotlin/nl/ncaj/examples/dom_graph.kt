package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import kotlin.math.sin

@OptIn(ExperimentalForeignApi::class)
fun exampleDomGraph() {
    var shift = 0

    val myGraph = GraphFn { w, h, out ->
        for (i in 0 until w) {
            var v = 0f
            v += 0.1f * sin((i + shift) * 0.1f)
            v += 0.2f * sin((i + shift + 10) * 0.15f)
            v += 0.1f * sin((i + shift) * 0.03f)
            v *= h
            v += 0.5f * h
            out[i] = v.toInt()
        }
    }

    val triangleGraph = GraphFn { w, h, out ->
        for (i in 0 until w) {
            out[i] = i % (h - 4) + 2
        }
    }

    val app = FtxUIApp.fitComponent()
    val component = renderer {
        val doc = hbox(
            vbox(
                graph(myGraph),
                separator(),
                graph(triangleGraph).inverted(),
            ).flex(),
            separator(),
            vbox(
                graph(myGraph).color(Color.BlueLight),
                separator(),
                graph(myGraph).color(Color.RedLight),
                separator(),
                graph(myGraph).color(Color.YellowLight),
            ).flex(),
        ).border().size(WidthOrHeight.Height, Constraint.GreaterThan, 40)
        shift++
        app.requestAnimationFrame()
        doc
    }

    val loop = FtxUILoop(app, component)
    while (!loop.hasQuitted()) {
        loop.runOnce()
        platform.posix.usleep(30_000u)
    }
    loop.destroy()
    app.destroy()
    component.destroy()
    myGraph.destroy()
    triangleGraph.destroy()
}