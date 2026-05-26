import nl.ncaj.*

internal class AppState(val entries: List<har.Entry>) {
    val selectedEntry = IntState(0)
    val tabSelected   = IntState(0)
    val leftSize      = IntState(50)
    val focusedPanel  = IntState(0)

    fun free() {
        selectedEntry.free()
        tabSelected.free()
        leftSize.free()
        focusedPanel.free()
    }
}
