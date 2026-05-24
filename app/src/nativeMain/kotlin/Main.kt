import har.Cookie
import har.Header
import har.Param
import har.parseHar
import kotlinx.cinterop.ExperimentalForeignApi
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

private val beige = Color.rgb(0xEB.toUByte(), 0xE2.toUByte(), 0xC3.toUByte())
private val yellow = Color.rgb(0xFF.toUByte(), 0xB7.toUByte(), 0x00.toUByte())

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    val path = args.firstOrNull() ?: run { println("Usage: har-viewer <file.har>"); return }
    val har = parseHar(path)
    val entries = har.log.entries
    if (entries.isEmpty()) {
        println("No entries found in HAR file."); return
    }

    val selectedEntry = IntState(0)
    val tabSelected = IntState(0)
    val leftSize = IntState(50)
    val hScrollOffset = IntState(0)
    val focusedPanel = IntState(0) // 0 = requests, 1 = details
    val bodyScrollX = IntState(0)
    val bodyScrollY = IntState(0)
    val reqHeadersScrollY = IntState(0)
    val respHeadersScrollY = IntState(0)
    val timingsScrollY = IntState(0)
    val searchQuery = StringState("")
    val leftSubFocus = IntState(0) // 0 = list, 1 = search input
    var bodyLastContent = ""
    var bodyHighlightedLines: List<List<StyledSpan>>? = null
    var reqBodyHighlightedLines: List<List<StyledSpan>>? = null
    var reqHeadersLastEntry = -1
    var respHeadersLastEntry = -1
    var timingsLastEntry = -1
    var lastSearchPattern = ""

    val maxMethodLen = entries.maxOf { it.request.method.length }

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

    val entryLabels = entries.map { "${it.request.method}  ${it.request.url}" }
    val requestListMenu = menu(entryLabels, selectedEntry)

    val leftSubPanel = tab(leftSubFocus)
    leftSubPanel.add(requestListMenu)
    leftSubPanel.add(searchInput)

    val requestListRenderer = leftSubPanel.decorateRender { subElem ->
        subElem.destroy()
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

        vbox(
            searchElem,
            listElem,
        ).flex()
            .window(run {
                val labelColor = if (focusedPanel.value == 0) Color.CyanLight else Color.GrayDark
                hbox(text(" [ "), text("r").underlined().bold().color(Color.GreenLight), text("equests").bold().color(labelColor), text(" ] "))
            })
            .let { if (focusedPanel.value != 0) it.color(Color.GrayDark) else it }
    }.catchEvent { event ->
        val filtered = getFilteredEntries()
        val currentPos = filtered.indexOfFirst { it.first == selectedEntry.value }
        val isSearchFocused = leftSubFocus.value == 1
        val maxHOffset = entries[selectedEntry.value].request.url.length
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
                hScrollOffset.value = 0
                when {
                    filtered.isEmpty() -> { }
                    currentPos < 0 -> selectedEntry.value = filtered.last().first
                    currentPos > 0 -> selectedEntry.value = filtered[currentPos - 1].first
                }
                true
            }
            event.isKey(Key.ArrowDown) -> {
                hScrollOffset.value = 0
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

    val tabLabels = listOf("Request", "Resp Headers", "Body", "Timings")
    val tabContainer = tab(tabSelected)

    fun headerRows(headers: List<Header>): List<Element> {
        if (headers.isEmpty()) return listOf(text("(none)").dim())
        val nameWidth = headers.maxOf { it.name.length }
        val chunkSize = 60
        val rows = mutableListOf<Element>()
        for ((i, header) in headers.withIndex()) {
            val chunks = header.value.chunked(chunkSize)
            chunks.forEachIndexed { j, chunk ->
                val nameCell = if (j == 0) text(header.name.padEnd(nameWidth)).bold().color(Color.CyanLight)
                else text(" ".repeat(nameWidth))
                rows.add(hbox(nameCell, text(" │ ").dim(), text(chunk).color(beige)))
            }
            if (i < headers.lastIndex) rows.add(separator())
        }
        return rows
    }

    fun cookieRows(cookies: List<Cookie>): List<Element> {
        if (cookies.isEmpty()) return listOf(text("(none)").dim())
        val nameWidth = cookies.maxOf { it.name.length }
        val rows = mutableListOf<Element>()
        for ((i, cookie) in cookies.withIndex()) {
            rows.add(hbox(text(cookie.name.padEnd(nameWidth)).bold().color(Color.CyanLight), text(" │ ").dim(), text(cookie.value).color(beige)))
            val meta = listOfNotNull(
                cookie.path?.let { "path=$it" },
                cookie.domain?.let { "domain=$it" },
                cookie.expires?.let { "expires=$it" },
                if (cookie.httpOnly == true) "HttpOnly" else null,
                if (cookie.secure == true) "Secure" else null,
            )
            if (meta.isNotEmpty()) rows.add(hbox(text(" ".repeat(nameWidth + 3)), text(meta.joinToString("  ")).dim()))
            if (i < cookies.lastIndex) rows.add(separator())
        }
        return rows
    }

    fun paramRows(params: List<Param>): List<Element> {
        if (params.isEmpty()) return emptyList()
        val nameWidth = params.maxOf { it.name.length }
        val rows = mutableListOf<Element>()
        for ((i, param) in params.withIndex()) {
            rows.add(hbox(text(param.name.padEnd(nameWidth)).bold().color(Color.CyanLight), text(" │ ").dim(), text(param.value ?: "").color(beige)))
            val meta = listOfNotNull(
                param.fileName?.let { "file=$it" },
                param.contentType?.let { "type=$it" },
            )
            if (meta.isNotEmpty()) rows.add(hbox(text(" ".repeat(nameWidth + 3)), text(meta.joinToString("  ")).dim()))
            if (i < params.lastIndex) rows.add(separator())
        }
        return rows
    }

    // Tab 0: Request overview + headers + body
    tabContainer.add(renderer {
        val idx = selectedEntry.value
        if (idx != reqHeadersLastEntry) { reqHeadersLastEntry = idx; reqHeadersScrollY.value = 0; reqBodyHighlightedLines = null }
        val entry = entries[idx]
        val req = entry.request
        val postData = req.postData
        val bodyLines = postData?.text?.lines() ?: emptyList()
        val allRows = buildList {
            // Overview
            add(hbox(text(" Overview").bold().color(Color.Black), filler()).bgcolor(beige))
            add(separatorEmpty())
            add(hbox(text("  Started    ").color(Color.CyanLight), text(entry.startedDateTime)))
            add(hbox(text("  HTTP       ").color(Color.CyanLight), text(req.httpVersion)))
            entry.serverIPAddress?.let { add(hbox(text("  Server IP  ").color(Color.CyanLight), text(it))) }
            entry.connection?.let { add(hbox(text("  Connection ").color(Color.CyanLight), text(it))) }
            entry.pageref?.let { add(hbox(text("  Page       ").color(Color.CyanLight), text(it))) }
            // Query string
            if (req.queryString.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Query Parameters").bold().color(Color.Black), text("  ${req.queryString.size}").color(Color.Black), filler()).bgcolor(beige))
                add(separatorEmpty())
                val nameWidth = req.queryString.maxOf { it.name.length }
                req.queryString.forEachIndexed { i, p ->
                    add(hbox(text(p.name.padEnd(nameWidth)).bold().color(Color.CyanLight), text(" │ ").dim(), text(p.value).color(beige)))
                    if (i < req.queryString.lastIndex) add(separator())
                }
            }
            // Headers
            add(separatorEmpty())
            val reqSizeInfo = buildString {
                if (req.headersSize >= 0) append("  ${req.headersSize}B headers")
                if (req.bodySize >= 0) append("  ${req.bodySize}B body")
            }
            add(hbox(
                text(" Request Headers").bold().color(Color.Black),
                text("  ${req.headers.size}").color(Color.Black),
                text(reqSizeInfo).color(Color.Black),
                filler(),
            ).bgcolor(beige))
            add(separatorEmpty())
            addAll(headerRows(req.headers))
            // Cookies
            if (req.cookies.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Request Cookies").bold().color(Color.Black), text("  ${req.cookies.size}").color(Color.Black), filler()).bgcolor(beige))
                add(separatorEmpty())
                addAll(cookieRows(req.cookies))
            }
            // Post body
            if (postData != null) {
                add(separatorEmpty())
                add(hbox(
                    text(" Request Body").bold().color(Color.Black),
                    if (postData.mimeType.isNotEmpty()) text("  ${postData.mimeType}").color(Color.Black) else text(""),
                    filler(),
                ).bgcolor(beige))
                add(separatorEmpty())
                if (postData.params.isNotEmpty()) {
                    addAll(paramRows(postData.params))
                } else if (bodyLines.isEmpty()) {
                    add(text("(empty)").dim())
                } else if (isJson(postData.mimeType)) {
                    if (reqBodyHighlightedLines == null) reqBodyHighlightedLines = tokenizeJsonLines(postData.text ?: "")
                    reqBodyHighlightedLines!!.forEach { spans -> add(renderHighlightedLine(spans)) }
                } else {
                    bodyLines.forEach { add(text(it)) }
                }
            }
        }
        vbox(*allRows.drop(reqHeadersScrollY.value).toTypedArray()).flex()
    })

    // Tab 1: Response headers + cookies
    tabContainer.add(renderer {
        val idx = selectedEntry.value
        if (idx != respHeadersLastEntry) { respHeadersLastEntry = idx; respHeadersScrollY.value = 0 }
        val entry = entries[idx]
        val resp = entry.response
        val status = resp.status
        val sColor = when {
            status < 200 -> Color.GrayLight
            status < 300 -> Color.GreenLight
            status < 400 -> Color.CyanLight
            status < 500 -> yellow
            else -> Color.RedLight
        }
        val respSizeInfo = buildString {
            if (resp.headersSize >= 0) append("  ${resp.headersSize}B headers")
            if (resp.bodySize >= 0) append("  ${resp.bodySize}B body")
        }
        val allRows = buildList {
            add(hbox(
                text(" Response Headers").bold().color(Color.Black),
                text("  "),
                text("$status${if (resp.statusText.isNotEmpty()) " ${resp.statusText}" else ""}").bold().color(sColor),
                text("  ${resp.httpVersion}").color(Color.Black),
                text("  ${resp.headers.size} headers").color(Color.Black),
                text(respSizeInfo).color(Color.Black),
                filler(),
            ).bgcolor(beige))
            add(separatorEmpty())
            addAll(headerRows(resp.headers))
            if (resp.redirectURL.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Redirect").bold().color(Color.Black), filler()).bgcolor(beige))
                add(separatorEmpty())
                add(hbox(text("  "), text(resp.redirectURL).color(Color.CyanLight)))
            }
            if (resp.cookies.isNotEmpty()) {
                add(separatorEmpty())
                add(hbox(text(" Response Cookies").bold().color(Color.Black), text("  ${resp.cookies.size}").color(Color.Black), filler()).bgcolor(beige))
                add(separatorEmpty())
                addAll(cookieRows(resp.cookies))
            }
        }
        vbox(*allRows.drop(respHeadersScrollY.value).toTypedArray()).flex()
    })

    // Tab 2: Response body — line-clipping driven by bodyScrollY/X state
    tabContainer.add(renderer {
        val entry = entries[selectedEntry.value]
        val content = entry.response.content
        val bodyText = content.text ?: "(no body)"
        if (bodyText != bodyLastContent) {
            bodyLastContent = bodyText
            bodyScrollX.value = 0
            bodyScrollY.value = 0
            bodyHighlightedLines = null
        }
        val mimeType = content.mimeType
        val isJsonBody = isJson(mimeType)
        if (isJsonBody && bodyHighlightedLines == null) bodyHighlightedLines = tokenizeJsonLines(bodyText)
        val lines = bodyText.lines()
        val visibleLines = lines.drop(bodyScrollY.value)
        val sizeInfo = buildString {
            append("  ${content.size}B")
            val comp = content.compression
            if (comp != null && comp > 0) append(" (${comp}B saved)")
            val enc = content.encoding
            if (!enc.isNullOrEmpty()) append("  $enc")
        }
        vbox(
            hbox(
                text(" Response Body").bold().color(Color.Black),
                if (mimeType.isNotEmpty()) text("  $mimeType").color(Color.Blue) else text(""),
                text(sizeInfo).color(Color.Black),
                text("  ${lines.size} lines").color(Color.Black),
                filler(),
            ).bgcolor(beige),
            separatorEmpty(),
            vbox(*visibleLines.mapIndexed { idx, line ->
                if (isJsonBody) {
                    val spans = bodyHighlightedLines!!.getOrElse(bodyScrollY.value + idx) { emptyList() }
                    renderHighlightedLine(clipSpans(spans, bodyScrollX.value))
                } else {
                    val displayed = when {
                        bodyScrollX.value <= 0 -> line
                        bodyScrollX.value >= line.length -> ""
                        else -> line.substring(bodyScrollX.value)
                    }
                    text(displayed)
                }
            }.toTypedArray()).flex(),
        ).flex()
    })

    // Tab 3: Timings + connection + cache
    tabContainer.add(renderer {
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
            add(hbox(text("    Last Access  ").dim(), text(cs.lastAccess)))
            add(hbox(text("    ETag         ").dim(), text(cs.eTag)))
            add(hbox(text("    Hit Count    ").dim(), text("${cs.hitCount}")))
            cs.expires?.let { add(hbox(text("    Expires      ").dim(), text(it))) }
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
                add(hbox(text(" Connection").bold().color(Color.Black), filler()).bgcolor(beige))
                add(separatorEmpty())
                entry.serverIPAddress?.let { add(hbox(text("  Server IP  ").color(Color.CyanLight), text(it))) }
                entry.connection?.let { add(hbox(text("  TCP conn   ").color(Color.CyanLight), text(it))) }
            }
            val cache = entry.cache
            if (cache.beforeRequest != null || cache.afterRequest != null) {
                add(separatorEmpty())
                add(hbox(text(" Cache").bold().color(Color.Black), filler()).bgcolor(beige))
                add(separatorEmpty())
                cache.beforeRequest?.let { addAll(cacheStateRows("Before Request", it)) }
                cache.afterRequest?.let { addAll(cacheStateRows("After Request", it)) }
            }
        }
        vbox(
            hbox(
                text(" Timings").bold().color(Color.Black),
                text("  Total: ").color(Color.Black),
                text("$totalMs ms").bold().color(yellow),
                filler(),
            ).bgcolor(beige),
            separatorEmpty(),
            vbox(*allRows.drop(timingsScrollY.value).toTypedArray()).flex(),
        ).flex()
    })

    fun renderTabBar(): Element {
        val detailsFocused = focusedPanel.value == 1
        val tabItems = tabLabels.mapIndexed { i, label ->
            val isActive = i == tabSelected.value
            when {
                isActive && detailsFocused -> hbox(
                    text(" "),
                    text("${i + 1}").bold().underlined().color(Color.White),
                    text(" $label ").bold().color(Color.White),
                ).bgcolor(Color.rgb(0xDA.toUByte(), 0x8E.toUByte(), 0xE7.toUByte()))
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

    val detailRenderer = renderer(tabContainer) {
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
            renderTabBar(),
            separator(),
            tabContainer.render().flex(),
        ).flex().window(run {
            val labelColor = if (focusedPanel.value == 1) Color.CyanLight else Color.GrayDark
            hbox(text(" [ "), text("d").underlined().bold().color(Color.GreenLight), text("etails").bold().color(labelColor), text(" ] "))
        })
        .let { if (focusedPanel.value != 1) it.color(Color.GrayDark) else it }
    }

    val panelRouter = tab(focusedPanel)
    panelRouter.add(requestListRenderer)
    panelRouter.add(detailRenderer)

    val mainContainer = renderer(panelRouter) {
        hbox(
            requestListRenderer.render().size(WidthOrHeight.Width, Constraint.Equal, leftSize.value),
            separatorEmpty(),
            detailRenderer.render().flex(),
        )
    }.catchEvent { event ->
        val searchActive = focusedPanel.value == 0 && leftSubFocus.value == 1
        val onDetails = focusedPanel.value == 1
        val onBody = onDetails && tabSelected.value == 2
        val onReqHeaders = onDetails && tabSelected.value == 0
        val onRespHeaders = onDetails && tabSelected.value == 1
        val onTimings = onDetails && tabSelected.value == 3
        when {
            event.isMouse -> true
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
                bodyScrollY.value += 1; true
            }
            (event.isKey(Key.ArrowLeft) || event.isKey("h")) && onBody -> {
                bodyScrollX.value = maxOf(0, bodyScrollX.value - 4); true
            }
            (event.isKey(Key.ArrowRight) || event.isKey("l")) && onBody -> {
                bodyScrollX.value += 4; true
            }
            (event.isKey(Key.ArrowUp) || event.isKey("k")) && onReqHeaders -> {
                reqHeadersScrollY.value = maxOf(0, reqHeadersScrollY.value - 1); true
            }
            (event.isKey(Key.ArrowDown) || event.isKey("j")) && onReqHeaders -> {
                reqHeadersScrollY.value += 1; true
            }
            (event.isKey(Key.ArrowUp) || event.isKey("k")) && onRespHeaders -> {
                respHeadersScrollY.value = maxOf(0, respHeadersScrollY.value - 1); true
            }
            (event.isKey(Key.ArrowDown) || event.isKey("j")) && onRespHeaders -> {
                respHeadersScrollY.value += 1; true
            }
            (event.isKey(Key.ArrowUp) || event.isKey("k")) && onTimings -> {
                timingsScrollY.value = maxOf(0, timingsScrollY.value - 1); true
            }
            (event.isKey(Key.ArrowDown) || event.isKey("j")) && onTimings -> {
                timingsScrollY.value += 1; true
            }
            else -> false
        }
    }

    val app = FtxUIApp.fullscreen()
    app.loop(mainContainer)

    app.destroy()
    selectedEntry.free()
    tabSelected.free()
    leftSize.free()
    hScrollOffset.free()
    focusedPanel.free()
    bodyScrollX.free()
    bodyScrollY.free()
    reqHeadersScrollY.free()
    respHeadersScrollY.free()
    timingsScrollY.free()
    searchQuery.free()
    leftSubFocus.free()
}
