package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*
import nl.ncaj.Constraint.GreaterThan
import nl.ncaj.Constraint.LessThan
import nl.ncaj.WidthOrHeight.Height
import nl.ncaj.WidthOrHeight.Width

@OptIn(ExperimentalForeignApi::class)
fun exampleSliderRgb() {
    val red = IntState(128)
    val green = IntState(25)
    val blue = IntState(100)

    val sliderRed = slider("Red  :", red, 0, 255, 1)
    val sliderGreen = slider("Green:", green, 0, 255, 1)
    val sliderBlue = slider("Blue :", blue, 0, 255, 1)

    val container = vertical(sliderRed, sliderGreen, sliderBlue)

    val rendererComp = renderer(container) {
        hbox(
            emptyElement()
                .size(Width, GreaterThan, 14)
                .size(Height, GreaterThan, 7)
                .bgcolor(Color.rgb(red.value.toUByte(), green.value.toUByte(), blue.value.toUByte())),
            separator(),
            vbox(
                sliderRed.render(),
                separator(),
                sliderGreen.render(),
                separator(),
                sliderBlue.render(),
                separator(),
                text("RGB = (${red.value},${green.value},${blue.value})"),
            ).xflex(),
        ).border().size(Width, LessThan, 80)
    }

    val screen = FtxUIApp.terminalOutput()
    screen.loop(rendererComp)
}