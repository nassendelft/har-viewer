package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleDropdownCustom() {
    val entries = listOf(
        "tribute", "clearance", "ally", "bend", "electronics",
        "module", "era", "cultural", "sniff", "nationalism",
        "negotiation", "deliver", "figure", "east", "tribute",
        "clearance", "ally", "bend", "electronics", "module",
    )

    val dropdown1 = dropdownCustom(
        entries = entries,
        transform = { open, checkbox, radiobox ->
            if (open) {
                vbox(
                    checkbox.inverted(),
                    radiobox.vscrollIndicator().frame().size(WidthOrHeight.Height, Constraint.LessThan, 10),
                    filler(),
                )
            } else {
                vbox(checkbox, filler())
            }
        }
    )

    val dropdown2 = dropdownCustom(
        entries = entries,
        transform = { open, checkbox, radiobox ->
            if (open) {
                vbox(
                    checkbox.inverted(),
                    radiobox.vscrollIndicator().frame()
                        .size(WidthOrHeight.Height, Constraint.LessThan, 10)
                        .bgcolor(Color.Blue),
                    filler(),
                )
            } else {
                vbox(checkbox.bgcolor(Color.Blue), filler())
            }
        }
    )

    val dropdown3 = dropdownCustom(
        entries = entries,
        entryTransform = { state ->
            var t = text(state.label).borderEmpty()
            if (state.active) t = t.bold()
            if (state.focused) t = t.inverted()
            t
        },
        transform = { open, checkbox, radiobox ->
            if (open) {
                vbox(
                    checkbox.borderEmpty().inverted(),
                    radiobox.vscrollIndicator().frame()
                        .size(WidthOrHeight.Height, Constraint.LessThan, 20)
                        .bgcolor(Color.Red),
                    filler(),
                )
            } else {
                vbox(checkbox.borderEmpty().bgcolor(Color.Red), filler())
            }
        }
    )

    val screen = FtxUIApp.fitComponent()
    screen.loop(horizontal(dropdown1, dropdown2, dropdown3))
}