package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomParagraph() {
    val p = "In probability theory and statistics, Bayes' theorem (alternatively Bayes' law or Bayes' rule) " +
            "describes the probability of an event, based on prior knowledge of conditions that might be related " +
            "to the event. For example, if cancer is related to age, then, using Bayes' theorem, a person's age " +
            "can be used to more accurately assess the probability that they have cancer, compared to the assessment " +
            "of the probability of cancer made without knowledge of the person's age."

    val rendererComp = renderer {
        vbox(
            hflow(paragraph(p)),
            separator(),
            hflow(paragraph(p)),
            separator(),
            hbox(
                hflow(paragraph(p)),
                separator(),
                hflow(paragraph(p)),
            ),
        ).border()
    }

    val screen = FtxUIApp.fitComponent()
    screen.loop(rendererComp)
}