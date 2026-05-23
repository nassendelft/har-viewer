package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomBorderColored() {
    fun makeBoxed() = vbox(
        text("borderLight").borderStyled(BorderStyle.Light, Color.Red),
        text("borderDashed").borderStyled(BorderStyle.Dashed, Color.Green),
        text("borderHeavy").borderStyled(BorderStyle.Heavy, Color.Blue),
        text("borderDouble").borderStyled(BorderStyle.Double, Color.Yellow),
        text("borderRounded").borderStyled(BorderStyle.Rounded, Color.Cyan),
    )

    val rendererComp = renderer {
        hbox(
            makeBoxed(),
            separator().color(Color.Red),
            makeBoxed(),
            separator().color(Color.Red),
            makeBoxed(),
        ).borderStyled(BorderStyle.Rounded, Color.Red)
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}
