package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleLinearGradientGallery() {
    val app = FtxUIApp.fullscreen()

    val angle = IntState(180)
    val start = FloatState(0f)
    val end = FloatState(1f)

    val sliderAngle = slider("angle", angle, 0, 360)
    val sliderStart = slider("start", start, 0f, 1f, 0.05f)
    val sliderEnd = slider("end  ", end, 0f, 1f, 0.05f)

    val layout = vertical(sliderAngle, sliderStart, sliderEnd)

    val component = renderer(layout) {
        val gradient = LinearGradient()
            .angle(angle.value.toFloat())
            .stop(Color.Blue, start.value)
            .stop(Color.Red, end.value)

        val background = text("Gradient").center().bgcolorLinearGradient(gradient)
        val result = vbox(background.flex(), layout.render()).flex()
        gradient.destroy()
        result
    }

    app.loop(component)
    app.destroy()
    component.destroy()
    angle.free()
    start.free()
    end.free()
}