package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleCheckbox() {
    val download = BoolState(false)
    val upload = BoolState(false)
    val ping = BoolState(false)

    val container = vertical(
        checkbox("Download", download),
        checkbox("Upload", upload),
        checkbox("Ping", ping),
    )

    val screen = FtxUIApp.fitComponent()
    screen.loop(container)
}