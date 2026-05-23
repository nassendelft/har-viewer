package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleRadiobox() {
    val radioboxList = listOf(
        "Use gcc",
        "Use clang",
        "Use emscripten",
        "Use tcc",
    )
    val selected = IntState(0)

    val screen = FtxUIApp.terminalOutput()
    screen.loop(radiobox(radioboxList, selected))
}