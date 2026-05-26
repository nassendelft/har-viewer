import nl.ncaj.*

internal class FilterState(methods: List<String>) {
    val showModal = BoolState(false)
    val methodStates: Map<String, BoolState> = methods.map { it.uppercase() }.distinct().sorted()
        .associateWith { BoolState(true) }
    val typeStates: Map<ResourceType, BoolState> = ResourceType.entries.associateWith { BoolState(true) }

    val isActive get() = methodStates.any { !it.value.value } || typeStates.any { !it.value.value }

    fun free() {
        showModal.free()
        methodStates.values.forEach { it.free() }
        typeStates.values.forEach { it.free() }
    }
}
