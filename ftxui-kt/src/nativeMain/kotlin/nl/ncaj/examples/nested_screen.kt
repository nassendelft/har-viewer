package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleNestedScreen() {
    fun nested(path: String) {
        val app = FtxUIApp.fitComponent()
        val backButton = button("Back", app.exitClosure())
        val goto1 = button("Goto /1", { nested("$path/1") })
        val goto2 = button("Goto /2", { nested("$path/2") })
        val goto3 = button("Goto /3", { nested("$path/3") })
        val layout = vertical(backButton, goto1, goto2, goto3)
        val component = renderer(layout) {
            vbox(
                text("path: $path"),
                separator(),
                backButton.render(),
                goto1.render(),
                goto2.render(),
                goto3.render(),
            ).border()
        }
        app.loop(component)
        app.destroy()
        component.destroy()
    }

    val app = FtxUIApp.fitComponent()
    val quitButton = button("Quit", app.exitClosure())
    val nestedButton = button("Nested", { nested("") })
    val layout = vertical(quitButton, nestedButton)
    app.loop(layout)
    app.destroy()
    layout.destroy()
}