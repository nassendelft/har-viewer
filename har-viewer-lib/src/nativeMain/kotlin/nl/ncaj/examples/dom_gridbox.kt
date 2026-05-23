package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDomGridbox() {
    fun cell(t: String) = text(t).border()

    val comp = renderer {
        gridbox(
            listOf(
                listOf(cell("north-west"), cell("north"), cell("north-east")),
                listOf(
                    cell("center-west"),
                    gridbox(
                        listOf(
                            listOf(cell("center-north-west"), cell("center-north-east")),
                            listOf(cell("center-south-west"), cell("center-south-east")),
                        )
                    ),
                    cell("center-east"),
                ),
                listOf(cell("south-west"), cell("south"), cell("south-east")),
            )
        )
    }
    val screen = FtxUIApp.fitComponent()
    screen.loop(comp)
}
