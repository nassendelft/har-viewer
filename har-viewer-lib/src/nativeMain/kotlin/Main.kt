import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun main() {
    var value = 0
    ftxui {
        val button = button("Click me", onClick = { value++ })
        renderer(button) {
            vbox {
                text("test1 $value")
                text("test2") { border() }
                render(button)
            }
        }
    }
}