package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleInputStyle() {
    val name = StringState()
    val email = StringState()

    val inputName = input(name, "your name")
    val inputEmail = input(email, "your email")

    val component = vertical(inputName, inputEmail)

    val r = renderer(component) {
        vbox(
            hbox(text("Name:  "), inputName.render()),
            hbox(text("Email: "), inputEmail.render()),
        ).border()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(r)
}