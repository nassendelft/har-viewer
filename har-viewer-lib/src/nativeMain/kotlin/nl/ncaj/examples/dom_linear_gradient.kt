package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomLinearGradient() {
    val deepPink = Color.rgb(255u, 20u, 147u)
    val deepSkyBlue = Color.rgb(0u, 191u, 255u)

    val gradient = LinearGradient()
        .angle(45f)
        .stop(deepPink)
        .stop(deepSkyBlue)

    val app = FtxUIApp.fitComponent()
    val component = renderer {
        text("gradient").center().bgcolorLinearGradient(gradient)
    }

    app.loop(component)
    app.destroy()
    component.destroy()
    gradient.destroy()
    deepPink.destroy()
    deepSkyBlue.destroy()
}