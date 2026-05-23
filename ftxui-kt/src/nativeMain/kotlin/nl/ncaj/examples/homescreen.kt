package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import kotlin.math.sin

@OptIn(ExperimentalForeignApi::class)
fun exampleHomescreen() {
    val app = FtxUIApp.fullscreen()
    var shift = 0

    val myGraph = GraphFn { w, h, out ->
        for (i in 0 until w) {
            var v = 0.5f
            v += 0.1f * sin((i + shift) * 0.1f)
            v += 0.2f * sin((i + shift + 10) * 0.15f)
            v += 0.1f * sin((i + shift) * 0.03f)
            v *= h
            out[i] = v.toInt()
        }
    }

    val htop = renderer {
        val frequency = vbox(
            text("Frequency [Mhz]").hcenter(),
            hbox(
                vbox(text("2400 "), filler(), text("1200 "), filler(), text("0 ")),
                graph(myGraph).flex(),
            ).flex(),
        )
        val utilization = vbox(
            text("Utilization [%]").hcenter(),
            hbox(
                vbox(text("100 "), filler(), text("50 "), filler(), text("0 ")),
                graph(myGraph).color(Color.RedLight).flex(),
            ).flex(),
        )
        val ram = vbox(
            text("Ram [Mo]").hcenter(),
            hbox(
                vbox(text("8192"), filler(), text("4096 "), filler(), text("0 ")),
                graph(myGraph).color(Color.BlueLight).flex(),
            ).flex(),
        )
        hbox(
            vbox(frequency.flex(), separator(), utilization.flex()).flex(),
            separator(),
            ram.flex(),
        ).flex()
    }

    val gaugeRenderer = renderer {
        fun renderGauge(delta: Int): Element {
            val progress = (shift + delta) % 500 / 500f
            return hbox(
                text("${(progress * 100).toInt()}% ").size(WidthOrHeight.Width, Constraint.Equal, 5),
                gauge(progress),
            )
        }
        vbox(
            renderGauge(0).color(Color.Black),
            renderGauge(100).color(Color.GrayDark),
            renderGauge(50).color(Color.GrayLight),
            renderGauge(6894).color(Color.White),
            separator(),
            renderGauge(6841).color(Color.Blue),
            renderGauge(9813).color(Color.BlueLight),
            renderGauge(98765).color(Color.Cyan),
            renderGauge(98).color(Color.CyanLight),
            renderGauge(9846).color(Color.Green),
            renderGauge(1122).color(Color.GreenLight),
            renderGauge(84).color(Color.Magenta),
            renderGauge(645).color(Color.MagentaLight),
            renderGauge(568).color(Color.Red),
            renderGauge(2222).color(Color.RedLight),
            renderGauge(220).color(Color.Yellow),
            renderGauge(348).color(Color.YellowLight),
        )
    }

    val spinnerRenderer = renderer {
        val entries = (0 until 22).map { i ->
            spinner(i, shift / 5).bold()
                .size(WidthOrHeight.Width, Constraint.GreaterThan, 2)
                .border()
        }
        hflow(*entries.toTypedArray())
    }

    val tabIndex = IntState(0)
    val tabEntries = listOf("htop", "gauge", "spinner")

    val tabSelection = menuHorizontalAnimated(tabEntries, tabIndex)

    val exitButton = button("Exit", app.exitClosure(), ButtonOption.animated())

    val tabContent = tab(tabIndex)
    tabContent.add(htop)
    tabContent.add(gaugeRenderer)
    tabContent.add(spinnerRenderer)

    val mainContainer = vertical(
        horizontal(tabSelection, exitButton),
        tabContent,
    )

    val mainRenderer = renderer(mainContainer) {
        vbox(
            text("FTXUI Demo").bold().hcenter(),
            hbox(tabSelection.render().flex(), exitButton.render()),
            tabContent.render().flex(),
        )
    }

    val loop = FtxUILoop(app, mainRenderer)
    while (!loop.hasQuitted()) {
        shift++
        app.requestAnimationFrame()
        loop.runOnce()
        platform.posix.usleep(16_667u)
    }

    loop.destroy()
    app.destroy()
    mainRenderer.destroy()
    tabIndex.free()
    myGraph.destroy()
}