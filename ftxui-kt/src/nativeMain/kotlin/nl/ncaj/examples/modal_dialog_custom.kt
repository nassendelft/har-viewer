package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleModalDialogCustom() {
    val depthState = IntState(0)
    var rating = "3/5 stars"

    val app = FtxUIApp.terminalOutput()

    val buttonRateFtxui = button("Rate FTXUI", { depthState.value = 1 })
    val buttonQuit = button("Quit", { app.exit() })

    val depth0Container = horizontal(buttonRateFtxui, buttonQuit)
    val depth0Renderer = renderer(depth0Container) {
        vbox(
            text("Modal dialog example"),
            separator(),
            text("FTXUI: $rating").bold(),
            filler(),
            hbox(
                buttonRateFtxui.render(),
                filler(),
                buttonQuit.render(),
            ),
        ).border().size(WidthOrHeight.Height, Constraint.GreaterThan, 18).center()
    }

    val ratingLabels = listOf("1/5 stars", "2/5 stars", "3/5 stars", "4/5 stars", "5/5 stars")
    val depth1Container = horizontal(
        *ratingLabels.map { label ->
            button(label, {
                rating = label
                depthState.value = 0
            })
        }.toTypedArray()
    )

    val depth1Renderer = renderer(depth1Container) {
        vbox(
            text("Do you like FTXUI?"),
            separator(),
            hbox(depth1Container.render()),
        ).border()
    }

    val mainContainer = tab(depthState).also { t ->
        t.add(depth0Renderer)
        t.add(depth1Renderer)
    }

    val mainRenderer = renderer(mainContainer) {
        val document = depth0Renderer.render()
        if (depthState.value == 1) {
            dbox(document, depth1Renderer.render().clearUnder().center())
        } else {
            document
        }
    }

    app.loop(mainRenderer)
}