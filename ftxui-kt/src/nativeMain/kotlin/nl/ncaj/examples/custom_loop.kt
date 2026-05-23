package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleCustomLoop() {
    val app = FtxUIApp.fitComponent()

    var customLoopCount = 0
    var frameCount = 0
    var eventCount = 0

    val component = renderer {
        frameCount++
        vbox(
            text("This demonstrates using a custom ftxui::Loop. It "),
            text("runs at 100 iterations per seconds. The FTXUI events "),
            text("are all processed once per iteration and a new frame "),
            text("is rendered as needed"),
            separator(),
            text("ftxui event count: $eventCount"),
            text("ftxui frame count: $frameCount"),
            text("Custom loop count: $customLoopCount"),
        ).border()
    }.catchEvent { _ ->
        eventCount++
        false
    }

    val loop = FtxUILoop(app, component)
    while (!loop.hasQuitted()) {
        customLoopCount++
        loop.runOnce()
        platform.posix.usleep(10_000u)
    }

    loop.destroy()
    app.destroy()
    component.destroy()
}