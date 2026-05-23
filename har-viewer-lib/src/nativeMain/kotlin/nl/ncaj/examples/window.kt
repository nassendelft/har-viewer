package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleWindow() {
    fun dummyWindowContent(): Component {
        val cb1 = BoolState(); val cb2 = BoolState(); val cb3 = BoolState()
        val sliderVal = FloatState(50f)
        val layout = vertical(
            checkbox("Check me", cb1),
            checkbox("Check me", cb2),
            checkbox("Check me", cb3),
            slider("Slider", sliderVal, 0f, 100f, 1f),
        )
        return layout
    }

    val win1Left = IntState(20); val win1Top = IntState(10)
    val win1Width = IntState(40); val win1Height = IntState(20)

    val window1 = windowComponent(WindowOptions(
        inner = dummyWindowContent(),
        title = "First window",
        left = win1Left, top = win1Top,
        width = win1Width, height = win1Height,
    ))

    val window2 = windowComponent(WindowOptions(
        inner = dummyWindowContent(),
        title = "My window",
        leftDefault = 40, topDefault = 20,
    ))

    val window3 = windowComponent(WindowOptions(
        inner = dummyWindowContent(),
        title = "My window",
        leftDefault = 60, topDefault = 30,
    ))

    val window4 = windowComponent(WindowOptions(inner = dummyWindowContent()))
    val window5 = windowComponent(WindowOptions())

    val windowContainer = stacked()
    windowContainer.add(window1)
    windowContainer.add(window2)
    windowContainer.add(window3)
    windowContainer.add(window4)
    windowContainer.add(window5)

    val displayWin1 = renderer {
        text("window_1: ${win1Width.value}x${win1Height.value} + ${win1Left.value},${win1Top.value}")
    }

    val layout = vertical(displayWin1, windowContainer)

    val app = FtxUIApp.fullscreen()
    app.loop(layout)
    app.destroy()
    layout.destroy()
    win1Left.free(); win1Top.free(); win1Width.free(); win1Height.free()
}