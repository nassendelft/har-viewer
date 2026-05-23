package nl.ncaj.examples

import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun exampleGallery() {
    fun wrap(name: String, component: Component): Component =
        component.decorateRender { inner ->
            hbox(text(name).size(WidthOrHeight.Width, Constraint.Equal, 8), separator(), inner).xflex()
        }

    val app = FtxUIApp.fitComponent()

    // Menu
    val menuSelected = IntState(0)
    val menuEntries = listOf("Menu 1", "Menu 2", "Menu 3", "Menu 4")
    val menuComp = wrap("Menu", menu(menuEntries, menuSelected))

    // Toggle
    val toggleSelected = IntState(0)
    val toggleEntries = listOf("Toggle_1", "Toggle_2")
    val toggleComp = wrap("Toggle", toggle(toggleEntries, toggleSelected))

    // Checkboxes
    val cb1 = BoolState(); val cb2 = BoolState(); val cb3 = BoolState(); val cb4 = BoolState()
    val checkboxes = wrap("Checkbox", vertical(
        checkbox("checkbox1", cb1),
        checkbox("checkbox2", cb2),
        checkbox("checkbox3", cb3),
        checkbox("checkbox4", cb4),
    ))

    // Radiobox
    val radioSelected = IntState(0)
    val radioEntries = listOf("Radiobox 1", "Radiobox 2", "Radiobox 3", "Radiobox 4")
    val radioComp = wrap("Radiobox", radiobox(radioEntries, radioSelected))

    // Input
    val inputLabel = StringState()
    val inputComp = wrap("Input", input(inputLabel, "placeholder"))

    // Button
    val buttonComp = wrap("Button", button("Quit", { app.exit() }))

    // Sliders
    val sliderVal1 = IntState(12); val sliderVal2 = IntState(56); val sliderVal3 = IntState(128)
    val slidersComp = wrap("Slider", vertical(
        slider("R:", sliderVal1, 0, 256, 1),
        slider("G:", sliderVal2, 0, 256, 1),
        slider("B:", sliderVal3, 0, 256, 1),
    ))

    // Dropdown
    val dropdownSelected = IntState(0)
    val dropdownEntries = listOf("Option 1", "Option 2", "Option 3")
    val dropdownComp = wrap("Dropdown", dropdown(dropdownEntries, dropdownSelected))

    val layout = vertical(
        menuComp,
        toggleComp,
        checkboxes,
        radioComp,
        inputComp,
        slidersComp,
        buttonComp,
        dropdownComp,
    )

    val component = renderer(layout) {
        vbox(
            menuComp.render(),
            separator(),
            toggleComp.render(),
            separator(),
            checkboxes.render(),
            separator(),
            radioComp.render(),
            separator(),
            inputComp.render(),
            separator(),
            slidersComp.render(),
            separator(),
            buttonComp.render(),
            separator(),
            dropdownComp.render(),
        ).xflex().size(WidthOrHeight.Width, Constraint.GreaterThan, 40).border()
    }

    app.loop(component)
}