import har.Cookie
import har.Param
import har.parseHar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.*
import nl.ncaj.*

private fun methodColor(method: String): Color = when (method.uppercase()) {
    "GET" -> Color.GreenLight
    "POST" -> yellow
    "PUT" -> Color.BlueLight
    "DELETE" -> Color.RedLight
    "PATCH" -> Color.CyanLight
    "HEAD", "OPTIONS" -> Color.MagentaLight
    else -> Color.GrayLight
}

private val beige = Color.rgb(0xEBu, 0xE2u, 0xC3u)
private val yellow = Color.rgb(0xFFu, 0xB7u, 0x00u)
private val black = Color.rgb(0u, 0u, 0u)

private fun cookieAnnotation(c: Cookie): String? = listOfNotNull(
    c.path?.let { "path=$it" },
    c.domain?.let { "domain=$it" },
    c.expires?.let { "expires=$it" },
    if (c.httpOnly == true) "HttpOnly" else null,
    if (c.secure == true) "Secure" else null,
).joinToString("  ").ifEmpty { null }

private fun paramAnnotation(p: Param): String? = listOfNotNull(
    p.fileName?.let { "file=$it" },
    p.contentType?.let { "type=$it" },
).joinToString("  ").ifEmpty { null }

private fun keyValueRows(
    pairs: List<Pair<String, String>>,
    panelWidth: Int = 0,
    annotations: List<String?> = emptyList(),
): List<Element> {
    if (pairs.isEmpty()) return listOf(text("(none)").dim())
    val nameWidth = pairs.maxOf { it.first.length }
    val rows = mutableListOf<Element>()
    for ((i, pair) in pairs.withIndex()) {
        val (name, value) = pair
        if (panelWidth > 0) {
            val chunkSize = maxOf(20, panelWidth - 1 - 2 - nameWidth - 3 - 1)
            value.chunked(chunkSize).forEachIndexed { j, chunk ->
                val nameCell = if (j == 0) text(name.padEnd(nameWidth)).bold().color(Color.CyanLight)
                               else text(" ".repeat(nameWidth))
                rows.add(hbox(nameCell, text(" │ ").dim(), text(chunk).color(beige)))
            }
        } else {
            rows.add(hbox(text(name.padEnd(nameWidth)).bold().color(Color.CyanLight), text(" │ ").dim(), text(value).color(beige)))
        }
        annotations.getOrNull(i)?.let { rows.add(hbox(text(" ".repeat(nameWidth + 3)), text(it).dim())) }
        if (i < pairs.lastIndex) rows.add(separator())
    }
    return rows
}

private fun vScrollBar(scrollY: Int, total: Int, visible: Int): Element {
    if (total <= visible) return vbox(*(0 until maxOf(1, visible)).map { text(" ") }.toTypedArray())
    val thumbH = maxOf(1, visible * visible / total)
    val thumbY = ((scrollY.toLong() * maxOf(0, visible - thumbH)) / maxOf(1, total - visible)).toInt()
    return vbox(*(0 until visible).map { i ->
        if (i in thumbY until thumbY + thumbH) text("▐").color(Color.GrayLight)
        else text("▕").dim()
    }.toTypedArray())
}

private fun hScrollBar(scrollX: Int, total: Int, visible: Int): Element {
    if (total <= visible) return hbox(*(0 until maxOf(1, visible)).map { text(" ") }.toTypedArray())
    val thumbW = maxOf(1, visible * visible / total)
    val thumbX = ((scrollX.toLong() * maxOf(0, visible - thumbW)) / maxOf(1, total - visible)).toInt()
    return hbox(*(0 until visible).map { i ->
        if (i in thumbX until thumbX + thumbW) text("▁").color(Color.GrayLight)
        else text("▁").dim()
    }.toTypedArray())
}

private fun renderTabBar(tabSelected: Int, focusedPanel: Int, tabLabels: List<String>): Element {
    val detailsFocused = focusedPanel == 1
    val tabItems = tabLabels.mapIndexed { i, label ->
        val isActive = i == tabSelected
        when {
            isActive && detailsFocused -> hbox(
                text(" "),
                text("${i + 1}").bold().underlined().color(Color.White),
                text(" $label ").bold().color(Color.White),
            ).bgcolor(Color.rgb(0xDAu, 0x8Eu, 0xE7u))
            isActive -> hbox(
                text(" "),
                text("${i + 1}").bold().underlined().color(Color.CyanLight),
                text(" $label ").bold(),
            )
            else -> hbox(
                text(" "),
                text("${i + 1}").underlined().color(Color.GrayDark),
                text(" $label ").color(Color.GrayDark),
            )
        }
    }
    val withSeps = tabItems.flatMap { listOf(it, text(" │ ").dim()) }.dropLast(1)
    return hbox(*withSeps.toTypedArray())
}

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    val path = args.firstOrNull() ?: run { println("Usage: har-viewer <file.har>"); return }
    val har = try {
        parseHar(path)
    } catch (e: IllegalStateException) {
        println("Error: ${e.message}"); return
    }
    val entries = har.log.entries
    if (entries.isEmpty()) {
        println("No entries found in HAR file."); return
    }

    var appRef: FtxUIApp? = null
    val bgScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val selectedEntry = IntState(0)
    val tabSelected = IntState(0)
    val leftSize = IntState(50)
    val focusedPanel = IntState(0)

    val hScrollOffset = IntState(0)
    val leftSubFocus = IntState(0)
    val searchQuery = StringState("")
    var lastSearchPattern = ""
    val searchInput = input(searchQuery, "filter (regex)…")

    fun getFilteredEntries() = run {
        val pat = searchQuery.value.trim()
        if (pat.isEmpty()) entries.mapIndexed { i, e -> i to e }
        else try {
            val re = Regex(pat, RegexOption.IGNORE_CASE)
            entries.mapIndexed { i, e -> i to e }.filter { (_, e) -> re.containsMatchIn(e.request.url) }
        } catch (_: Exception) {
            entries.mapIndexed { i, e -> i to e }
        }
    }

    fun buildRequestListContent(subElem: Element): Element {
        subElem.destroy()
        val maxMethodLen = entries.maxOf { it.request.method.length }
        val isFocused = focusedPanel.value == 0
        val isSearchFocused = leftSubFocus.value == 1
        val filtered = getFilteredEntries()

        if (searchQuery.value != lastSearchPattern) {
            lastSearchPattern = searchQuery.value
            if (filtered.none { it.first == selectedEntry.value } && filtered.isNotEmpty()) {
                selectedEntry.value = filtered[0].first
            }
        }

        val scroll = hScrollOffset.value
        val listItems = filtered.map { (origIdx, entry) ->
            val method = entry.request.method.uppercase()
            val color = methodColor(method)
            val isSelected = origIdx == selectedEntry.value
            val url = entry.request.url
            val prefix = if (scroll > 0) "…" else " "
            val displayUrl = if (scroll > 0) url.drop(scroll) else url
            val methodElem = text(" ${method.padEnd(maxMethodLen)} ").bold()
                .let { if (isSelected && isFocused) it.bgcolor(color).color(Color.White) else it.color(color) }
            val urlElem = text("$prefix$displayUrl${" ".repeat(500)}")
                .let { if (isSelected && isFocused) it.bgcolor(Color.GrayDark).color(Color.White) else it }
                .xflex()
            hbox(methodElem, urlElem)
                .let { if (isSelected) it.focus() else it }
        }

        val searchBarColor = when {
            !isFocused -> Color.GrayDark
            isSearchFocused -> Color.CyanLight
            else -> Color.Default
        }
        val searchElem = hbox(
            text(" / ").color(searchBarColor),
            searchInput.render(),
        ).border().color(searchBarColor)

        val listElem = when {
            filtered.isEmpty() && searchQuery.value.isNotBlank() ->
                vbox(text("  no matches").dim()).flex()
            else ->
                vbox(*listItems.toTypedArray())
                    .vscrollIndicator()
                    .yframe()
                    .flex()
        }

        val maxUrlLen = filtered.maxOfOrNull { (_, e) -> e.request.url.length } ?: 0
        val visibleW = maxOf(1, leftSize.value - 2)

        return vbox(
            searchElem,
            listElem,
            hScrollBar(hScrollOffset.value, maxUrlLen, visibleW),
        ).flex()
            .window(run {
                val labelColor = if (focusedPanel.value == 0) Color.CyanLight else Color.GrayDark
                hbox(text(" [ "), text("r").underlined().bold().color(Color.GreenLight), text("equests").bold().color(labelColor), text(" ] "))
            })
            .let { if (focusedPanel.value != 0) it.color(Color.GrayDark) else it }
    }

    val reqHeadersScrollY = IntState(0)
    var reqHeadersLastEntry = -1
    var reqBodyHighlightedLines: List<List<StyledSpan>>? = null
    var reqHeadersRowCount = 0

    fun requestTabContent(): Element {
        val idx = selectedEntry.value
        if (idx != reqHeadersLastEntry) { reqHeadersLastEntry = idx; reqHeadersScrollY.value = 0; reqBodyHighlightedLines = null }
        val entry = entries[idx]
        val req = entry.request
        val postData = req.postData
        val bodyLines = postData?.text?.replace("\t", "    ")?.lines() ?: emptyList()
        val panelWidth = Terminal.size().dimx - leftSize.value
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
                    if (reqBodyHighlightedLines == null) reqBodyHighlightedLines = reqHighlighter.tokenizeLines(postData.text?.replace("\t", "    ") ?: "")
                    val reqBodyWidth = maxOf(1, Terminal.size().dimx - leftSize.value - 2)
                    reqBodyHighlightedLines!!.forEach { spans -> add(renderHighlightedLine(clipSpans(spans, 0, reqBodyWidth))) }
                } else {
                    bodyLines.forEach { add(text(it)) }
                }
            }
        }
        reqHeadersRowCount = allRows.size
        val reqH = Terminal.size().dimy - 6
        return hbox(
            vbox(*allRows.drop(reqHeadersScrollY.value).take(reqH).toTypedArray()).flex(),
            vScrollBar(reqHeadersScrollY.value, allRows.size, reqH),
        ).flex()
    }

    val respHeadersScrollY = IntState(0)
    var respHeadersLastEntry = -1
    var respHeadersRowCount = 0

    fun responseHeadersTabContent(): Element {
        val idx = selectedEntry.value
        if (idx != respHeadersLastEntry) { respHeadersLastEntry = idx; respHeadersScrollY.value = 0 }
        val entry = entries[idx]
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
        val panelWidth = Terminal.size().dimx - leftSize.value
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
        respHeadersRowCount = allRows.size
        val respH = Terminal.size().dimy - 6
        return hbox(
            vbox(*allRows.drop(respHeadersScrollY.value).take(respH).toTypedArray()).flex(),
            vScrollBar(respHeadersScrollY.value, allRows.size, respH),
        ).flex()
    }

    val bodyScrollX = IntState(0)
    val bodyScrollY = IntState(0)
    val bodyPrettify = BoolState(false)
    var bodyLastContent = ""
    var bodyLastPrettify = false
    var bodyIsJson = false
    var bodyHighlightedLines: List<List<StyledSpan>>? = null
    var bodyPendingLines: List<List<StyledSpan>>? = null
    var bodyTokenizingJob: Job? = null
    var bodyLineCount = 0
    var bodyMaxLineWidth = 0

    fun bodyTabContent(): Element {
        val entry = entries[selectedEntry.value]
        val content = entry.response.content
        val bodyText = (content.text ?: "(no body)").replace("\t", "    ")
        val mimeType = content.mimeType
        val isJson = JsonHighlighter.accepts(mimeType)
        bodyIsJson = isJson
        if (bodyText != bodyLastContent) bodyPrettify.value = false
        if (bodyText != bodyLastContent || bodyPrettify.value != bodyLastPrettify) {
            bodyLastContent = bodyText
            bodyLastPrettify = bodyPrettify.value
            bodyScrollX.value = 0
            bodyScrollY.value = 0
            bodyHighlightedLines = null
            bodyTokenizingJob?.cancel()
            bodyTokenizingJob = null
            bodyPendingLines = null
        }
        val displayText = if (bodyPrettify.value && isJson) JsonHighlighter.prettyPrint(bodyText) else bodyText
        val bodyHighlighter = highlighterFor(mimeType)
        bodyPendingLines?.let {
            bodyHighlightedLines = it
            bodyPendingLines = null
            bodyTokenizingJob = null
        }
        if (bodyHighlighter != null && bodyHighlightedLines == null && bodyTokenizingJob == null) {
            val highlighter = bodyHighlighter
            val text = displayText
            bodyTokenizingJob = bgScope.launch {
                bodyPendingLines = highlighter.tokenizeLines(text)
                appRef?.requestAnimationFrame()
            }
        }
        val lines = displayText.lines()
        bodyLineCount = lines.size
        bodyMaxLineWidth = lines.maxOfOrNull { it.length } ?: 0
        val panelWidth = maxOf(1, Terminal.size().dimx - leftSize.value - 2 - 1)
        val bodyH = maxOf(1, Terminal.size().dimy - 8)
        val visibleLines = lines.drop(bodyScrollY.value).take(bodyH)
        val sizeInfo = buildString {
            append("  ${content.size}B")
            val comp = content.compression
            if (comp != null && comp > 0) append(" (${comp}B saved)")
            val enc = content.encoding
            if (!enc.isNullOrEmpty()) append("  $enc")
        }
        return vbox(
            hbox(
                text(" Response Body").bold().color(black),
                if (mimeType.isNotEmpty()) hbox(text("  "),text(mimeType).color(black).underlined()) else text(""),
                text(sizeInfo).color(black),
                text("  ${lines.size} lines").color(black),
                if (isJson) text("  [p] pretty").let {
                    if (bodyPrettify.value) it.color(yellow) else it.color(black).dim()
                } else text(""),
                filler(),
            ).bgcolor(beige),
            separatorEmpty(),
            hbox(
                vbox(*visibleLines.mapIndexed { idx, line ->
                    val highlighted = bodyHighlightedLines
                    if (bodyHighlighter != null && highlighted != null) {
                        val spans = highlighted.getOrElse(bodyScrollY.value + idx) { emptyList() }
                        renderHighlightedLine(clipSpans(spans, bodyScrollX.value, panelWidth))
                    } else {
                        val displayed = when {
                            bodyScrollX.value <= 0 -> line
                            bodyScrollX.value >= line.length -> ""
                            else -> line.substring(bodyScrollX.value)
                        }
                        text(displayed)
                    }
                }.toTypedArray()).flex(),
                vScrollBar(bodyScrollY.value, bodyLineCount, bodyH),
            ).flex(),
            hScrollBar(bodyScrollX.value, bodyMaxLineWidth, panelWidth),
        ).flex()
    }

    val timingsScrollY = IntState(0)
    var timingsLastEntry = -1
    var timingsRowCount = 0

    fun timingsTabContent(): Element {
        val idx = selectedEntry.value
        if (idx != timingsLastEntry) { timingsLastEntry = idx; timingsScrollY.value = 0 }
        val entry = entries[idx]
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
        timingsRowCount = allRows.size
        val timingsH = maxOf(1, Terminal.size().dimy - 8)
        return vbox(
            hbox(
                text(" Diagnostics").bold().color(black),
                text("  Total: ").color(black),
                text("$totalMs ms").bold().color(yellow),
                filler(),
            ).bgcolor(beige),
            separatorEmpty(),
            hbox(
                vbox(*allRows.drop(timingsScrollY.value).take(timingsH).toTypedArray()).flex(),
                vScrollBar(timingsScrollY.value, allRows.size, timingsH),
            ).flex(),
        ).flex()
    }

    fun buildRequestPanel(): Component {
        val entryLabels = entries.map { "${it.request.method}  ${it.request.url}" }
        val requestListMenu = menu(entryLabels, selectedEntry)

        val leftSubPanel = tab(leftSubFocus)
        leftSubPanel.add(requestListMenu)
        leftSubPanel.add(searchInput)

        return leftSubPanel.decorateRender { buildRequestListContent(it) }
            .catchEvent { event ->
                val filtered = getFilteredEntries()
                val currentPos = filtered.indexOfFirst { it.first == selectedEntry.value }
                val isSearchFocused = leftSubFocus.value == 1
                val maxUrlLen = filtered.maxOfOrNull { (_, e) -> e.request.url.length } ?: 0
                val maxHOffset = maxOf(0, maxUrlLen - maxOf(1, leftSize.value - 2))
                when {
                    event.isMouse -> true
                    event.isKey("/") && !isSearchFocused -> { leftSubFocus.value = 1; true }
                    event.isKey(Key.Escape) && isSearchFocused -> { leftSubFocus.value = 0; true }
                    event.isKey(Key.Return) && isSearchFocused -> { leftSubFocus.value = 0; true }
                    (event.isKey(Key.ArrowRight) || event.isKey("l")) && !isSearchFocused -> {
                        hScrollOffset.value = minOf(hScrollOffset.value + 5, maxHOffset); true
                    }
                    (event.isKey(Key.ArrowLeft) || event.isKey("h")) && !isSearchFocused -> {
                        hScrollOffset.value = maxOf(hScrollOffset.value - 5, 0); true
                    }
                    event.isKey(Key.ArrowUp) -> {
                        when {
                            filtered.isEmpty() -> { }
                            currentPos < 0 -> selectedEntry.value = filtered.last().first
                            currentPos > 0 -> selectedEntry.value = filtered[currentPos - 1].first
                        }
                        true
                    }
                    event.isKey(Key.ArrowDown) -> {
                        when {
                            filtered.isEmpty() -> { }
                            currentPos < 0 -> selectedEntry.value = filtered.first().first
                            currentPos < filtered.size - 1 -> selectedEntry.value = filtered[currentPos + 1].first
                        }
                        true
                    }
                    event.isKey(Key.Return) && !isSearchFocused -> { focusedPanel.value = 1; true }
                    else -> false
                }
            }
    }

    fun buildDetailsPanel(): Component {
        val tabContainer = tab(tabSelected)
        tabContainer.add(renderer { requestTabContent() })
        tabContainer.add(renderer { responseHeadersTabContent() })
        tabContainer.add(renderer { bodyTabContent() })
        tabContainer.add(renderer { timingsTabContent() })

        val tabLabels = listOf("Request", "Resp Headers", "Body", "Diagnostics")
        return renderer(tabContainer) {
            val entry = entries[selectedEntry.value]
            val method = entry.request.method.uppercase()
            val mColor = methodColor(method)
            val detailsFocused = focusedPanel.value == 1
            vbox(
                hbox(
                    text(" $method ").bold().bgcolor(mColor).color(Color.White)
                        .let { if (!detailsFocused) it.dim() else it },
                    text("  ${entry.request.url}"),
                ),
                separator(),
                renderTabBar(tabSelected.value, focusedPanel.value, tabLabels),
                separator(),
                tabContainer.render().flex(),
            ).flex().window(run {
                val labelColor = if (focusedPanel.value == 1) Color.CyanLight else Color.GrayDark
                hbox(text(" [ "), text("d").underlined().bold().color(Color.GreenLight), text("etails").bold().color(labelColor), text(" ] "))
            })
            .let { if (focusedPanel.value != 1) it.color(Color.GrayDark) else it }
        }
    }

    val requestPanel = buildRequestPanel()
    val detailsPanel = buildDetailsPanel()

    val panelRouter = tab(focusedPanel)
    panelRouter.add(requestPanel)
    panelRouter.add(detailsPanel)

    val mainContainer = renderer(panelRouter) {
        hbox(
            requestPanel.render().size(WidthOrHeight.Width, Constraint.Equal, leftSize.value),
            detailsPanel.render().flex(),
        )
    }.catchEvent { event ->
        val searchActive = focusedPanel.value == 0 && leftSubFocus.value == 1
        val onDetails = focusedPanel.value == 1
        val onBody = onDetails && tabSelected.value == 2
        val onReqHeaders = onDetails && tabSelected.value == 0
        val onRespHeaders = onDetails && tabSelected.value == 1
        val onTimings = onDetails && tabSelected.value == 3
        val contentHeight = Terminal.size().dimy - 6
        when {
            event.isKey("r") && !searchActive -> { focusedPanel.value = 0; true }
            event.isKey("d") && !searchActive -> { focusedPanel.value = 1; true }
            event.isKey("1") && !searchActive -> { tabSelected.value = 0; focusedPanel.value = 1; true }
            event.isKey("2") && !searchActive -> { tabSelected.value = 1; focusedPanel.value = 1; true }
            event.isKey("3") && !searchActive -> { tabSelected.value = 2; focusedPanel.value = 1; true }
            event.isKey("4") && !searchActive -> { tabSelected.value = 3; focusedPanel.value = 1; true }
            (event.isKey(Key.ArrowUp) || event.isKey("k")) && onBody -> {
                bodyScrollY.value = maxOf(0, bodyScrollY.value - 1); true
            }
            (event.isKey(Key.ArrowDown) || event.isKey("j")) && onBody -> {
                val maxScroll = maxOf(0, bodyLineCount - (contentHeight - 2))
                bodyScrollY.value = minOf(maxScroll, bodyScrollY.value + 1); true
            }
            (event.isKey(Key.ArrowLeft) || event.isKey("h")) && onBody -> {
                bodyScrollX.value = maxOf(0, bodyScrollX.value - 4); true
            }
            (event.isKey(Key.ArrowRight) || event.isKey("l")) && onBody -> {
                val panelW = maxOf(1, Terminal.size().dimx - leftSize.value - 2 - 1)
                val maxScrollX = maxOf(0, bodyMaxLineWidth - panelW)
                bodyScrollX.value = minOf(maxScrollX, bodyScrollX.value + 4); true
            }
            event.isKey("p") && onBody && bodyIsJson -> { bodyPrettify.value = !bodyPrettify.value; true }
            (event.isKey(Key.ArrowUp) || event.isKey("k")) && onReqHeaders -> {
                reqHeadersScrollY.value = maxOf(0, reqHeadersScrollY.value - 1); true
            }
            (event.isKey(Key.ArrowDown) || event.isKey("j")) && onReqHeaders -> {
                val maxScroll = maxOf(0, reqHeadersRowCount - contentHeight)
                reqHeadersScrollY.value = minOf(maxScroll, reqHeadersScrollY.value + 1); true
            }
            (event.isKey(Key.ArrowUp) || event.isKey("k")) && onRespHeaders -> {
                respHeadersScrollY.value = maxOf(0, respHeadersScrollY.value - 1); true
            }
            (event.isKey(Key.ArrowDown) || event.isKey("j")) && onRespHeaders -> {
                val maxScroll = maxOf(0, respHeadersRowCount - contentHeight)
                respHeadersScrollY.value = minOf(maxScroll, respHeadersScrollY.value + 1); true
            }
            (event.isKey(Key.ArrowUp) || event.isKey("k")) && onTimings -> {
                timingsScrollY.value = maxOf(0, timingsScrollY.value - 1); true
            }
            (event.isKey(Key.ArrowDown) || event.isKey("j")) && onTimings -> {
                val maxScroll = maxOf(0, timingsRowCount - (contentHeight - 2))
                timingsScrollY.value = minOf(maxScroll, timingsScrollY.value + 1); true
            }
            else -> false
        }
    }

    val app = FtxUIApp.fullscreen().also { appRef = it }
    app.loop(mainContainer)

    bgScope.cancel()
    app.destroy()
    selectedEntry.free()
    tabSelected.free()
    leftSize.free()
    hScrollOffset.free()
    focusedPanel.free()
    bodyScrollX.free()
    bodyScrollY.free()
    bodyPrettify.free()
    reqHeadersScrollY.free()
    respHeadersScrollY.free()
    timingsScrollY.free()
    searchQuery.free()
    leftSubFocus.free()
}
