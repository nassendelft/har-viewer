import nl.ncaj.*

internal class FilterPanel(private val filterState: FilterState) {

    fun build(): Component {
        val methodCheckboxes = filterState.methodStates.map { (method, state) -> checkbox(method, state) }
        val typeCheckboxes = filterState.typeStates.map { (type, state) -> checkbox(type.label, state) }

        val methodContainer = vertical(*methodCheckboxes.toTypedArray())
        val typeContainer = vertical(*typeCheckboxes.toTypedArray())
        val allContainer = horizontal(methodContainer, typeContainer)

        return renderer(allContainer) {
            vbox(
                hbox(
                    text(" Methods ").bold().color(Color.CyanLight)
                        .size(WidthOrHeight.Width, Constraint.Equal, 14),
                    text(" Types ").bold().color(Color.CyanLight),
                ),
                separator(),
                hbox(
                    methodContainer.render()
                        .size(WidthOrHeight.Width, Constraint.Equal, 14),
                    separatorLight(),
                    typeContainer.render(),
                ),
                separator(),
                hbox(
                    text(" [").dim(), text("a").bold(), text("]ll  ").dim(),
                    text("[").dim(), text("n").bold(), text("]one  ").dim(),
                    text("[Esc] close").dim(),
                ),
            ).border()
        }.catchEvent { event ->
            when {
                event.isKey(Key.Escape) -> { filterState.showModal.value = false; true }
                event.isKey("a") -> {
                    filterState.methodStates.values.forEach { it.value = true }
                    filterState.typeStates.values.forEach { it.value = true }
                    true
                }
                event.isKey("n") -> {
                    filterState.methodStates.values.forEach { it.value = false }
                    filterState.typeStates.values.forEach { it.value = false }
                    true
                }
                else -> false
            }
        }
    }
}
