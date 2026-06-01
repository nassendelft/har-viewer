import nl.ncaj.*

internal class ResponseHeadersTab(private val appState: AppState) {
    private val scrollY = IntState(0)
    private var lastEntry = -1
    var rowCount = 0
        private set

    fun build(): Component = renderer { render() }

    fun handleScrollEvent(event: FtxUIEvent, prevKey: String, contentHeight: Int): Boolean =
        handleScrollEvents(event, prevKey, scrollY, rowCount, contentHeight)

    fun free() = scrollY.close()

    private fun render(): Element {
        val idx = appState.selectedEntry.value
        if (idx != lastEntry) { lastEntry = idx; scrollY.value = 0 }
        val entry = appState.entries[idx]
        val resp = entry.response
        val status = resp.status
        val sColor = when {
            status < 200 -> Color.GrayDark
            status < 300 -> Color.Green
            status < 400 -> Color.Cyan
            status < 500 -> Color.Yellow
            else -> Color.Red
        }
        val respSizeInfo = buildString {
            if (resp.headersSize >= 0) append("  ${resp.headersSize}B headers")
            if (resp.bodySize >= 0) append("  ${resp.bodySize}B body")
        }
        val panelWidth = Terminal.size().dimx - appState.leftSize.value
        val allRows = buildList {
            add(hbox(
                text(" Response Headers").bold().color(black),
                text("  "),
                text(" $status${if (resp.statusText.isNotEmpty()) " ${resp.statusText}" else ""} ").bold().bgcolor(sColor).color(Color.White),
                text("  ${resp.httpVersion}").color(black),
                text("  ${resp.headers.size} headers").color(black),
                text(respSizeInfo).color(black),
                filler(),
            ).bgcolor(beige))
            add(separatorEmpty())
            addAll(keyValueRows(resp.headers.map { it.name to it.value }, panelWidth))
            if (resp.redirectURL.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Redirect").bold().color(black), filler()).bgcolor(beige))
                add(separatorEmpty())
                add(hbox(text("  "), text(resp.redirectURL).color(Color.CyanLight)))
            }
            if (resp.cookies.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Response Cookies").bold().color(black), text("  ${resp.cookies.size}").color(black), filler()).bgcolor(beige))
                add(separatorEmpty())
                addAll(keyValueRows(resp.cookies.map { it.name to it.value }, annotations = resp.cookies.map { cookieAnnotation(it) }))
            }
        }
        rowCount = allRows.size
        val respH = Terminal.size().dimy - 4
        return hbox(
            vbox(separatorEmpty(), *allRows.drop(scrollY.value).take(respH).toTypedArray()).flex(),
            vScrollBar(scrollY.value, allRows.size, respH),
        ).flex()
    }
}
