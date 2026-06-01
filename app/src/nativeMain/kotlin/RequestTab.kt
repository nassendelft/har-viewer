import highlight.*
import nl.ncaj.*

internal class RequestTab(private val appState: AppState) {
    private val scrollY = IntState(0)
    private val showRawBody = BoolState(false)
    private var lastEntry = -1
    private var lastShowRaw = false
    private var bodyHighlightedLines: List<List<StyledSpan>>? = null
    var rowCount = 0
        private set

    fun build(): Component = renderer { render() }

    fun handleScrollEvent(event: FtxUIEvent, prevKey: String, contentHeight: Int): Boolean {
        val entry = appState.entries[appState.selectedEntry.value]
        val hasParams = entry.request.postData?.params?.isNotEmpty() == true
        if (event.isKey("w") && hasParams) { showRawBody.value = !showRawBody.value; return true }
        return handleScrollEvents(event, prevKey, scrollY, rowCount, contentHeight)
    }

    fun free() { scrollY.close(); showRawBody.close() }

    private fun render(): Element {
        val idx = appState.selectedEntry.value
        val rawNow = showRawBody.value
        if (idx != lastEntry) { lastEntry = idx; scrollY.value = 0; showRawBody.value = false; bodyHighlightedLines = null; lastShowRaw = false }
        else if (rawNow != lastShowRaw) { bodyHighlightedLines = null; lastShowRaw = rawNow }
        val entry = appState.entries[idx]
        val req = entry.request
        val postData = req.postData
        val rawBodyText = postData?.let {
            it.text?.takeIf { t -> t.isNotEmpty() }
                ?: if (it.params.isNotEmpty()) it.params.joinToString("&") { p -> "${p.name}=${p.value ?: ""}" }
                else null
        }
        val bodyLines = rawBodyText?.replace("\t", "    ")?.lines() ?: emptyList()
        val panelWidth = Terminal.size().dimx - appState.leftSize.value
        val allRows = buildList {
            add(hbox(text(" Overview").bold().color(black), filler()).bgcolor(beige))
            add(separatorEmpty())
            addAll(keyValueRows(buildList {
                add("Method" to req.method.uppercase())
                add("URL" to displayUrl(req.url).substringBefore('?').substringBefore('#'))
                add("Started" to entry.startedDateTime)
                add("HTTP" to req.httpVersion)
                entry.serverIPAddress?.let { add("Server IP" to it) }
                entry.connection?.let { add("Connection" to it) }
                entry.pageref?.let { add("Page" to it) }
            }, panelWidth))
            if (req.queryString.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Query Parameters").bold().color(black), text("  ${req.queryString.size}").color(black), filler()).bgcolor(beige))
                add(separatorEmpty())
                addAll(keyValueRows(req.queryString.map { it.name to it.value }, panelWidth))
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
                addAll(keyValueRows(req.cookies.map { it.name to it.value }, panelWidth, annotations = req.cookies.map { cookieAnnotation(it) }))
            }
            if (postData != null) {
                add(separatorEmpty())
                val hasParams = postData.params.isNotEmpty()
                val rawToggleText = if (hasParams) {
                    text("  ra[w]").let {
                        if (showRawBody.value) it.color(yellow) else it.color(black).dim()
                    }
                } else text("")
                add(hbox(
                    text(" Request Body").bold().color(black),
                    if (postData.mimeType.isNotEmpty()) text("  ${postData.mimeType}").color(black) else text(""),
                    rawToggleText,
                    filler(),
                ).bgcolor(beige))
                add(separatorEmpty())
                val reqHighlighter = highlighterFor(postData.mimeType)
                if (hasParams && !showRawBody.value) {
                    addAll(keyValueRows(postData.params.map { it.name to (it.value ?: "") }, panelWidth, annotations = postData.params.map { paramAnnotation(it) }))
                } else if (bodyLines.isEmpty()) {
                    add(text("(empty)").dim())
                } else if (reqHighlighter != null) {
                    if (bodyHighlightedLines == null) bodyHighlightedLines = reqHighlighter.tokenizeLines(rawBodyText?.replace("\t", "    ") ?: "")
                    val reqBodyWidth = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2)
                    bodyHighlightedLines!!.forEach { spans -> add(renderHighlightedLine(clipSpans(spans, 0, reqBodyWidth))) }
                } else {
                    bodyLines.forEach { add(text(it)) }
                }
            }
        }
        rowCount = allRows.size
        val reqH = Terminal.size().dimy - 4
        return hbox(
            vbox(separatorEmpty(), *allRows.drop(scrollY.value).take(reqH).toTypedArray()).flex(),
            vScrollBar(scrollY.value, allRows.size, reqH),
        ).flex()
    }
}
