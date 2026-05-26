import nl.ncaj.*

internal fun handleScrollEvents(
    event: FtxUIEvent,
    prevKey: String,
    scrollY: IntState,
    rowCount: Int,
    contentHeight: Int,
): Boolean {
    val maxScroll = maxOf(0, rowCount - contentHeight)
    val halfPage = maxOf(1, contentHeight / 2)
    return when {
        event.isKey(Key.ArrowUp) || event.isKey("k") -> { scrollY.value = maxOf(0, scrollY.value - 1); true }
        event.isKey(Key.ArrowDown) || event.isKey("j") -> { scrollY.value = minOf(maxScroll, scrollY.value + 1); true }
        event.isKey(Key.CtrlD) -> { scrollY.value = minOf(maxScroll, scrollY.value + halfPage); true }
        event.isKey(Key.CtrlU) -> { scrollY.value = maxOf(0, scrollY.value - halfPage); true }
        event.isKey(Key.CtrlF) || event.isKey(Key.PageDown) -> { scrollY.value = minOf(maxScroll, scrollY.value + contentHeight); true }
        event.isKey(Key.CtrlB) || event.isKey(Key.PageUp) -> { scrollY.value = maxOf(0, scrollY.value - contentHeight); true }
        event.isKey("G") -> { scrollY.value = maxScroll; true }
        event.isKey("g") && prevKey == "g" -> { scrollY.value = 0; true }
        else -> false
    }
}
