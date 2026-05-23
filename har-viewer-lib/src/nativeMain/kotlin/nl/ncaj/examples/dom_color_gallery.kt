package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomColorGallery() {
    val colors = listOf(
        Color.Default to "Default",
        Color.Black to "Black",
        Color.GrayDark to "GrayDark",
        Color.GrayLight to "GrayLight",
        Color.White to "White",
        Color.Blue to "Blue",
        Color.BlueLight to "BlueLight",
        Color.Cyan to "Cyan",
        Color.CyanLight to "CyanLight",
        Color.Green to "Green",
        Color.GreenLight to "GreenLight",
        Color.Magenta to "Magenta",
        Color.MagentaLight to "MagentaLight",
        Color.Red to "Red",
        Color.RedLight to "RedLight",
        Color.Yellow to "Yellow",
        Color.YellowLight to "YellowLight",
    )

    val screen = FtxUIApp.fitComponent()
    screen.loop(renderer {
        vbox(
            text("16 color palette:"),
            separator(),
            hbox(
                vbox(*colors.map { (c, name) -> text(name).color(c) }.toTypedArray()),
                vbox(*colors.map { (c, name) -> text(name).bgcolor(c) }.toTypedArray())
            )
        )
    })
}
