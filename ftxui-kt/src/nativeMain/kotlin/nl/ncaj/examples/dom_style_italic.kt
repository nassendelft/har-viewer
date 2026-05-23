package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomStyleItalic() {
    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer { hbox(text("This text is "), text("italic").italic(), text(". Do you like it?")) })
}
