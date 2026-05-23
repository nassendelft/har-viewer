package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomStyleGallery() {
    val rendererComp = renderer {
        hbox(
            text("normal"),                                          text(" "),
            text("bold").bold(),                                    text(" "),
            text("italic").italic(),                                text(" "),
            text("dim").dim(),                                      text(" "),
            text("inverted").inverted(),                            text(" "),
            text("underlined").underlined(),                        text(" "),
            text("underlinedDouble").underlinedDouble(),            text(" "),
            text("blink").blink(),                                  text(" "),
            text("strikethrough").strikethrough(),                  text(" "),
            text("color").color(Color.Blue),                        text(" "),
            text("bgcolor").bgcolor(Color.Blue),                    text(" "),
            text("hyperlink").hyperlink("https://github.com/ArthurSonzogni/FTXUI"),
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
