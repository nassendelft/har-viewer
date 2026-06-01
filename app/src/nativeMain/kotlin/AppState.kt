import nl.ncaj.*

internal class AppState(val entries: List<har.Entry>) {
    val selectedEntry = IntState(0)
    val tabSelected   = IntState(0)
    val leftSize      = IntState(50)
    val focusedPanel  = IntState(0)
    val filterState   = FilterState(entries.map { it.request.method })

    fun close() {
        selectedEntry.close()
        tabSelected.close()
        leftSize.close()
        focusedPanel.close()
        filterState.close()
    }
}
