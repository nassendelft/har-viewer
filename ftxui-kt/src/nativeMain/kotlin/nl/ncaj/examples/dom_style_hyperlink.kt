package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomStyleHyperlink() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer {
        hbox(
            text("This text is an "),
            text("hyperlink").hyperlink("https://www.google.com"),
            text(". Do you like it?")
        )
    })
}
