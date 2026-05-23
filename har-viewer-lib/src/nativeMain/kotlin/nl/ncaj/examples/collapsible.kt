package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleCollapsible() {
    fun inner(vararg children: Component): Component {
        val vlist = vertical(*children)
        return renderer(vlist) {
            hbox(
                text(" "),
                vlist.render(),
            )
        }
    }

    fun empty() = renderer { emptyElement() }

    val component = collapsible(
        "Collapsible 1",
        inner(
            collapsible(
                "Collapsible 1.1",
                inner(
                    collapsible("Collapsible 1.1.1", empty(), BoolState(false)),
                    collapsible("Collapsible 1.1.2", empty(), BoolState(false)),
                    collapsible("Collapsible 1.1.3", empty(), BoolState(false)),
                ),
                BoolState(false)
            ),
            collapsible(
                "Collapsible 1.2",
                inner(
                    collapsible("Collapsible 1.2.1", empty(), BoolState(false)),
                    collapsible("Collapsible 1.2.2", empty(), BoolState(false)),
                    collapsible("Collapsible 1.2.3", empty(), BoolState(false)),
                ),
                BoolState(false)
            ),
            collapsible(
                "Collapsible 1.3",
                inner(
                    collapsible("Collapsible 1.3.1", empty(), BoolState(false)),
                    collapsible("Collapsible 1.3.2", empty(), BoolState(false)),
                    collapsible("Collapsible 1.3.3", empty(), BoolState(false)),
                ),
                BoolState(false)
            ),
        ),
        BoolState(false)
    )

    FtxUIApp.fitComponent().loop(component)
}