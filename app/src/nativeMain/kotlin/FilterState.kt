import nl.ncaj.*

internal class FilterState(methods: List<String>) {
    val showModal = BoolState(false)
    val methodStates: Map<String, BoolState> = methods.map { it.uppercase() }.distinct().sorted()
        .associateWith { BoolState(false) }
    val typeStates: Map<ResourceType, BoolState> = ResourceType.entries.associateWith { BoolState(false) }

    val isActive get() = methodStates.any { it.value.value } || typeStates.any { it.value.value }

    fun close() {
        showModal.close()
        methodStates.values.forEach { it.close() }
        typeStates.values.forEach { it.close() }
    }
}
