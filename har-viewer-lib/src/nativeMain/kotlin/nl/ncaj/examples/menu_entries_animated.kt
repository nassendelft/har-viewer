package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMenuEntriesAnimated() {
    val selected = IntState(0)

    val menu = tab(selected).also { container ->
        container.add(menuEntry(" 1. rear", animatedMenuEntryColors(Color.Red)))
        container.add(menuEntry(" 2. drown", animatedMenuEntryColors(Color.Yellow)))
        container.add(menuEntry(" 3. nail", animatedMenuEntryColors(Color.Green)))
        container.add(menuEntry(" 4. quit", animatedMenuEntryColors(Color.Cyan)))
        container.add(menuEntry(" 5. decorative", animatedMenuEntryColors(Color.Blue)))
        container.add(menuEntry(" 7. costume"))
        container.add(menuEntry(" 8. pick"))
        container.add(menuEntry(" 9. oral"))
        container.add(menuEntry("11. minister"))
        container.add(menuEntry("12. football"))
        container.add(menuEntry("13. welcome"))
        container.add(menuEntry("14. copper"))
        container.add(menuEntry("15. inhabitant"))
    }

    val r = renderer(menu) {
        vbox(
            hbox(text("selected = "), text(selected.value.toString())),
            separator(),
            menu.render().frame(),
        ).border().bgcolor(Color.Black)
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(r)
}