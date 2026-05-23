package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.LessThan
import nl.ncaj.WidthOrHeight.Height

@OptIn(ExperimentalForeignApi::class)
fun exampleMenuEntries() {
    val selected = IntState(0)
    val menuComponent = vertical(
        menuEntry(" 1. improve"),
        menuEntry(" 2. tolerant"),
        menuEntry(" 3. career"),
        menuEntry(" 4. cast"),
        menuEntry(" 5. question"),
        renderer { separator() },
        menuEntry(" 6. rear"),
        menuEntry(" 7. drown"),
        menuEntry(" 8. nail"),
        menuEntry(" 9. quit"),
        menuEntry("10. decorative"),
        renderer { separator() },
        menuEntry("11. costume"),
        menuEntry("12. pick"),
        menuEntry("13. oral"),
        menuEntry("14. minister"),
        menuEntry("15. football"),
    )

    val component = renderer(menuComponent) {
        vbox(
            hbox(text("selected = "), text(selected.value.toString())),
            separator(),
            menuComponent.render().frame().size(Height, LessThan, 10)
        ).border()
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(component)
    selected.free()
}