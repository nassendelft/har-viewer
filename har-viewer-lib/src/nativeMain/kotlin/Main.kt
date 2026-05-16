import ftxui_c.ftxui_color_t.*
import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.border
import nl.ncaj.color
import nl.ncaj.flex
import nl.ncaj.ftxui

@OptIn(ExperimentalForeignApi::class)
fun main() {
    ftxui {
        vbox {
            hbox {
                text("one") { border() }
                text("two") { border().flex() }
                text("three") { border().flex() }
            }

            gauge(0.25) { color(FTXUI_COLOR_RED) }
            gauge(0.50) { color(FTXUI_COLOR_WHITE) }
            gauge(0.75) { color(FTXUI_COLOR_BLUE) }
        }
    }
}