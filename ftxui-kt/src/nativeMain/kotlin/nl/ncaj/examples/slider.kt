package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleSlider() {
    val value = IntState(50)
    val sliderComp = slider("Value:", value, 0, 100, 1)

    val screen = FtxUIApp.terminalOutput()
    screen.loop(sliderComp)
}