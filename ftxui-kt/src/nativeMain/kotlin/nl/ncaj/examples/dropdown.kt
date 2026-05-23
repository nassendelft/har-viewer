package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDropdown() {
    val entries = listOf(
        "tribute", "clearance", "ally", "bend", "electronics",
        "module", "era", "cultural", "sniff", "nationalism",
        "negotiation", "deliver", "figure", "east", "tribute",
        "clearance", "ally", "bend", "electronics", "module",
        "era", "cultural", "sniff", "nationalism", "negotiation",
        "deliver", "figure", "east", "tribute", "clearance",
        "ally", "bend", "electronics", "module", "era",
        "cultural", "sniff", "nationalism", "negotiation", "deliver",
        "figure", "east",
    )

    val selected1 = IntState(0)
    val selected2 = IntState(0)
    val selected3 = IntState(0)
    val selected4 = IntState(0)

    val layout = vertical(
        horizontal(
            dropdown(entries, selected1),
            dropdown(entries, selected2),
        ),
        horizontal(
            dropdown(entries, selected3),
            dropdown(entries, selected4),
        ),
    )

    val screen = FtxUIApp.fitComponent()
    screen.loop(layout)
}