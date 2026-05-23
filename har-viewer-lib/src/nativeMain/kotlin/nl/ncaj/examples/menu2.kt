package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleMenu2() {
    val leftEntries = listOf("0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%")
    val rightEntries = listOf("0%", "1%", "2%", "3%", "4%", "5%", "6%", "7%", "8%", "9%", "10%")

    val leftSelected = IntState(0)
    val rightSelected = IntState(0)
    val leftMenu = menu(leftEntries, leftSelected)
    val rightMenu = menu(rightEntries, rightSelected)

    val container = horizontal(leftMenu, rightMenu)

    val component = renderer(container) {
        val sum = leftSelected.value * 10 + rightSelected.value
        vbox(
            hbox(
                vbox(text("Percentage by 10%").hcenter().bold(), separator(), leftMenu.render()),
                separator(),
                vbox(text("Percentage by 1%").hcenter().bold(), separator(), rightMenu.render()),
                separator()
            ),
            separator(),
            vbox(
                hbox(text(" gauge : "), gauge(sum / 100.0f)),
                hbox(text("  text : "), text("$sum %"))
            )
        ).border()
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(component)
    leftSelected.free()
    rightSelected.free()
}