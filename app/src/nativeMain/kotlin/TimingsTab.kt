import nl.ncaj.*

internal class TimingsTab(private val appState: AppState) {
    private val scrollY = IntState(0)
    private var lastEntry = -1
    var rowCount = 0
        private set

    fun build(): Component = renderer { render() }

    fun handleScrollEvent(event: FtxUIEvent, prevKey: String, contentHeight: Int): Boolean =
        handleScrollEvents(event, prevKey, scrollY, rowCount, contentHeight)

    fun free() = scrollY.free()

    private fun render(): Element {
        val idx = appState.selectedEntry.value
        if (idx != lastEntry) { lastEntry = idx; scrollY.value = 0 }
        val entry = appState.entries[idx]
        val t = entry.timings
        val positiveMs = listOfNotNull(t.blocked, t.dns, t.connect, t.ssl, t.send, t.wait, t.receive).filter { it >= 0 }
        val totalMs = positiveMs.sum()
        val maxMs = positiveMs.maxOrNull() ?: 1.0

        fun bar(ms: Double): String {
            val filled = ((ms / maxMs) * 20).toInt().coerceIn(0, 20)
            return "█".repeat(filled) + "░".repeat(20 - filled)
        }

        fun timingRow(label: String, ms: Double): Element =
            if (ms < 0) {
                hbox(text(" ${label.padEnd(8)}").color(Color.GrayDark), text("  n/a").dim())
            } else {
                hbox(text(" ${label.padEnd(8)}").color(Color.CyanLight), text("  ${bar(ms)} ").color(Color.Blue), text("$ms ms").color(yellow))
            }

        fun cacheStateRows(label: String, cs: har.CacheState): List<Element> = buildList {
            add(hbox(text("  $label").bold().color(Color.CyanLight)))
            addAll(keyValueRows(buildList {
                add("Last Access" to cs.lastAccess)
                add("ETag" to cs.eTag)
                add("Hit Count" to "${cs.hitCount}")
                cs.expires?.let { add("Expires" to it) }
            }))
        }

        val allRows = buildList {
            addAll(listOf(
                timingRow("Blocked", t.blocked ?: -1.0),
                timingRow("DNS",     t.dns     ?: -1.0),
                timingRow("Connect", t.connect ?: -1.0),
                timingRow("SSL",     t.ssl     ?: -1.0),
                timingRow("Send",    t.send),
                timingRow("Wait",    t.wait),
                timingRow("Receive", t.receive),
            ))
            if (entry.serverIPAddress != null || entry.connection != null) {
                add(separatorEmpty())
                add(hbox(text(" Connection").bold().color(black), filler()).bgcolor(beige))
                add(separatorEmpty())
                addAll(keyValueRows(buildList {
                    entry.serverIPAddress?.let { add("Server IP" to it) }
                    entry.connection?.let { add("TCP conn" to it) }
                }))
            }
            val cache = entry.cache
            if (cache.beforeRequest != null || cache.afterRequest != null) {
                add(separatorEmpty())
                add(hbox(text(" Cache").bold().color(black), filler()).bgcolor(beige))
                add(separatorEmpty())
                cache.beforeRequest?.let { addAll(cacheStateRows("Before Request", it)) }
                cache.afterRequest?.let { addAll(cacheStateRows("After Request", it)) }
            }
        }
        rowCount = allRows.size
        val timingsH = maxOf(1, Terminal.size().dimy - 6)
        return vbox(
            separatorEmpty(),
            hbox(
                text(" Diagnostics").bold().color(black),
                text("  Total: ").color(black),
                text("$totalMs ms").bold().color(yellow),
                filler(),
            ).bgcolor(beige),
            separatorEmpty(),
            hbox(
                vbox(*allRows.drop(scrollY.value).take(timingsH).toTypedArray()).flex(),
                vScrollBar(scrollY.value, allRows.size, timingsH),
            ).flex(),
        ).flex()
    }
}
