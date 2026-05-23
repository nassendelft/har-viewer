package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomHtmlLike() {
    var frame = 0

    val img1 = { text("img").border() }
    val img2 = { vbox(text("big"), text("image")).border() }

    val app = FtxUIApp.fitComponent()
    val component = renderer {
        val document = hflow(
            paragraph("Hello world! Here is an image:"), img1(),
            paragraph(" Here is a text "), text("underlined ").underlined(),
            paragraph(" Here is a text "), text("bold ").bold(),
            paragraph("Hello world! Here is an image:"), img2(),
            paragraph(
                "Le Lorem Ipsum est simplement du faux texte employe dans la " +
                "composition et la mise en page avant impression. Le Lorem " +
                "Ipsum est le faux texte standard de l'imprimerie depuis les " +
                "annees 1500, quand un imprimeur anonyme assembla ensemble " +
                "des morceaux de texte pour realiser un livre specimen de " +
                "polices de texte."
            ),
            paragraph(" Here is a text "), text("dim ").dim(),
            paragraph("Hello world! Here is an image:"), img1(),
            paragraph(" Here is a text "), text("red ").color(Color.Red),
            paragraph(" A spinner "), spinner(6, frame / 10),
        ).border()
        app.requestAnimationFrame()
        frame++
        document
    }

    app.loop(component)
    app.destroy()
    component.destroy()
}