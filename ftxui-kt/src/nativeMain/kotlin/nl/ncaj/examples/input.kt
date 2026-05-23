package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleInput() {
    val firstName = StringState()
    val lastName = StringState()
    val password = StringState()
    val phoneNumber = StringState()

    val inputFirstName = input(firstName, "first name")
    val inputLastName = input(lastName, "last name")
    val inputPassword = inputPassword(password, "password")
    var inputPhoneNumber = input(phoneNumber, "phone number")
    inputPhoneNumber = inputPhoneNumber.catchEvent { event ->
        event.isCharacter && !event.character.first().isDigit()
    }
    inputPhoneNumber = inputPhoneNumber.catchEvent { event ->
        event.isCharacter && phoneNumber.value.length > 10
    }

    val component = vertical(
        inputFirstName,
        inputLastName,
        inputPassword,
        inputPhoneNumber,
    )

    val r = renderer(component) {
        vbox(
            hbox(text(" First name : "), inputFirstName.render()),
            hbox(text(" Last name  : "), inputLastName.render()),
            hbox(text(" Password   : "), inputPassword.render()),
            hbox(text(" Phone num  : "), inputPhoneNumber.render()),
            separator(),
            text("Hello ${firstName.value} ${lastName.value}"),
            text("Your password is ${password.value}"),
            text("Your phone number is ${phoneNumber.value}"),
        ).border()
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(r)
}