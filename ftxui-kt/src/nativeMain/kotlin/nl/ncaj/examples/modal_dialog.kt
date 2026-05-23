package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.GreaterThan
import nl.ncaj.WidthOrHeight.Height
import nl.ncaj.WidthOrHeight.Width

@OptIn(ExperimentalForeignApi::class)
fun exampleModalDialog() {
    val buttonStyle = ButtonOption.animated()

    val modalShown = BoolState(false)
    val showModal: () -> Unit = { modalShown.value = true }
    val hideModal: () -> Unit = { modalShown.value = false }

    val app = FtxUIApp.terminalOutput()
    val exitApp = app.exitClosure()

    val mainButtons = vertical(
        button("Show modal", showModal, buttonStyle),
        button("Quit", exitApp, buttonStyle),
    )
    val mainComponent = renderer(mainButtons) {
        vbox(
            text("Main component"),
            separator(),
            mainButtons.render(),
        )
            .size(Width, GreaterThan, 15)
            .size(Height, GreaterThan, 15)
            .border()
            .center()
    }

    val modalButtons = vertical(
        button("Do nothing", {}, buttonStyle),
        button("Quit modal", hideModal, buttonStyle),
    )
    val modalComponent = renderer(modalButtons) {
        vbox(
            text("Modal component "),
            separator(),
            modalButtons.render(),
        )
            .size(Width, GreaterThan, 30)
            .border()
    }

    val combined = mainComponent.modal(modalComponent, modalShown)

    app.loop(combined)
}