package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMenuStyle() {
    val entries = listOf("Monkey", "Dog", "Cat", "Bird", "Elephant", "Cat")

    val selected1 = IntState(0)
    val selected2 = IntState(0)
    val selected3 = IntState(0)
    val selected4 = IntState(0)

    val vmenu1 = menu(entries, selected1)
    val vmenu2 = menu(entries, selected2)
    val hmenu1 = menuHorizontal(entries, selected3)
    val hmenu2 = menuHorizontalAnimated(entries, selected4)
    val hmenu3 = menuToggle(entries, selected4)

    val layout = vertical(
        horizontal(vmenu1, vmenu2),
        hmenu1,
        hmenu2,
        hmenu3,
    )

    val component = renderer(layout) {
        hbox(
            vbox(
                hbox(
                    vmenu1.render(),
                    separator(),
                    vmenu2.render(),
                ),
                separator(),
                hmenu1.render(),
                separator(),
                hmenu2.render(),
                separator(),
                hmenu3.render(),
            ).border(),
            filler(),
        )
    }

    val app = FtxUIApp.terminalOutput()
    app.loop(component)
    app.destroy()
    component.destroy()
    selected1.free(); selected2.free(); selected3.free(); selected4.free()
}