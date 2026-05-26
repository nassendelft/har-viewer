import nl.ncaj.*

internal class RequestTab(private val appState: AppState) {
    private val scrollY = IntState(0)
    private var lastEntry = -1
    private var bodyHighlightedLines: List<List<StyledSpan>>? = null
    var rowCount = 0
        private set

    fun build(): Component = renderer { render() }

    fun handleScrollEvent(event: FtxUIEvent, prevKey: String, contentHeight: Int): Boolean =
        handleScrollEvents(event, prevKey, scrollY, rowCount, contentHeight)

    fun free() = scrollY.free()

    private fun render(): Element {
        val idx = appState.selectedEntry.value
        if (idx != lastEntry) { lastEntry = idx; scrollY.value = 0; bodyHighlightedLines = null }
        val entry = appState.entries[idx]
        val req = entry.request
        val postData = req.postData
        val bodyLines = postData?.text?.replace("\t", "    ")?.lines() ?: emptyList()
        val panelWidth = Terminal.size().dimx - appState.leftSize.value
        val allRows = buildList {
            add(hbox(text(" Overview").bold().color(black), filler()).bgcolor(beige))
            add(separatorEmpty())
            addAll(keyValueRows(buildList {
                add("Started" to entry.startedDateTime)
                add("HTTP" to req.httpVersion)
                entry.serverIPAddress?.let { add("Server IP" to it) }
                entry.connection?.let { add("Connection" to it) }
                entry.pageref?.let { add("Page" to it) }
            }))
            if (req.queryString.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Query Parameters").bold().color(black), text("  ${req.queryString.size}").color(black), filler()).bgcolor(beige))
                add(separatorEmpty())
                addAll(keyValueRows(req.queryString.map { it.name to it.value }))
            }
            add(separatorEmpty())
            val reqSizeInfo = buildString {
                if (req.headersSize >= 0) append("  ${req.headersSize}B headers")
                if (req.bodySize >= 0) append("  ${req.bodySize}B body")
            }
            add(hbox(
                text(" Request Headers").bold().color(black),
                text("  ${req.headers.size}").color(black),
                text(reqSizeInfo).color(black),
                filler(),
            ).bgcolor(beige))
            add(separatorEmpty())
            addAll(keyValueRows(req.headers.map { it.name to it.value }, panelWidth))
            if (req.cookies.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Request Cookies").bold().color(black), text("  ${req.cookies.size}").color(black), filler()).bgcolor(beige))
                add(separatorEmpty())
                addAll(keyValueRows(req.cookies.map { it.name to it.value }, annotations = req.cookies.map { cookieAnnotation(it) }))
            }
            if (postData != null) {
                add(separatorEmpty())
                add(hbox(
                    text(" Request Body").bold().color(black),
                    if (postData.mimeType.isNotEmpty()) text("  ${postData.mimeType}").color(black) else text(""),
                    filler(),
                ).bgcolor(beige))
                add(separatorEmpty())
                val reqHighlighter = highlighterFor(postData.mimeType)
                if (postData.params.isNotEmpty()) {
                    addAll(keyValueRows(postData.params.map { it.name to (it.value ?: "") }, annotations = postData.params.map { paramAnnotation(it) }))
                } else if (bodyLines.isEmpty()) {
                    add(text("(empty)").dim())
                } else if (reqHighlighter != null) {
                    if (bodyHighlightedLines == null) bodyHighlightedLines = reqHighlighter.tokenizeLines(postData.text?.replace("\t", "    ") ?: "")
                    val reqBodyWidth = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2)
                    bodyHighlightedLines!!.forEach { spans -> add(renderHighlightedLine(clipSpans(spans, 0, reqBodyWidth))) }
                } else {
                    bodyLines.forEach { add(text(it)) }
                }
            }
        }
        rowCount = allRows.size
        val reqH = Terminal.size().dimy - 6
        return hbox(
            vbox(*allRows.drop(scrollY.value).take(reqH).toTypedArray()).flex(),
            vScrollBar(scrollY.value, allRows.size, reqH),
        ).flex()
    }
}
