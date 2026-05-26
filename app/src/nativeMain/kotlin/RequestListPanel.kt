import nl.ncaj.*

internal class RequestListPanel(private val appState: AppState) {
    private val hScrollOffset = IntState(0)
    private val leftSubFocus  = IntState(0)
    private val searchQuery   = StringState("")
    private val searchInput   = input(searchQuery, "search (regex)…")
    private var lastKey = ""
    private val filterPanel   = FilterPanel(appState.filterState)

    val isSearchActive: Boolean get() = appState.focusedPanel.value == 0 &&
        (leftSubFocus.value == 1 || appState.filterState.showModal.value)

    fun build(): Component {
        val entryLabels = appState.entries.map { "${it.request.method}  ${it.request.url}" }
        val requestListMenu = menu(entryLabels, appState.selectedEntry)

        val leftSubPanel = tab(leftSubFocus)
        leftSubPanel.add(requestListMenu)
        leftSubPanel.add(searchInput)

        val listComponent = leftSubPanel.decorateRender { buildRequestListContent(it) }
            .catchEvent { event ->
                val filtered = getFilteredEntries()
                val currentPos = filtered.indexOfFirst { it.first == appState.selectedEntry.value }
                val isSearchFocused = leftSubFocus.value == 1
                val maxUrlLen = filtered.maxOfOrNull { (_, e) -> e.request.url.length } ?: 0
                val maxHOffset = maxOf(0, maxUrlLen - maxOf(1, appState.leftSize.value - 2))
                val listPageSize = maxOf(1, Terminal.size().dimy - 6)
                val halfListPage = maxOf(1, listPageSize / 2)
                val prevReqKey = lastKey
                if (!event.isMouse && !isSearchFocused) lastKey = event.input
                fun moveBy(delta: Int) {
                    if (filtered.isEmpty()) return
                    val target = (currentPos + delta).coerceIn(0, filtered.size - 1)
                    appState.selectedEntry.value = filtered[target].first
                }
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
                    (event.isKey(Key.ArrowUp) || event.isKey("k")) && !isSearchFocused -> {
                        when {
                            filtered.isEmpty() -> { }
                            currentPos < 0 -> appState.selectedEntry.value = filtered.last().first
                            currentPos > 0 -> appState.selectedEntry.value = filtered[currentPos - 1].first
                        }
                        true
                    }
                    (event.isKey(Key.ArrowDown) || event.isKey("j")) && !isSearchFocused -> {
                        when {
                            filtered.isEmpty() -> { }
                            currentPos < 0 -> appState.selectedEntry.value = filtered.first().first
                            currentPos < filtered.size - 1 -> appState.selectedEntry.value = filtered[currentPos + 1].first
                        }
                        true
                    }
                    event.isKey(Key.CtrlD) && !isSearchFocused -> { moveBy(halfListPage); true }
                    event.isKey(Key.CtrlU) && !isSearchFocused -> { moveBy(-halfListPage); true }
                    (event.isKey(Key.CtrlF) || event.isKey(Key.PageDown)) && !isSearchFocused -> { moveBy(listPageSize); true }
                    (event.isKey(Key.CtrlB) || event.isKey(Key.PageUp)) && !isSearchFocused -> { moveBy(-listPageSize); true }
                    event.isKey("G") && !isSearchFocused -> {
                        if (filtered.isNotEmpty()) appState.selectedEntry.value = filtered.last().first; true
                    }
                    event.isKey("g") && !isSearchFocused && prevReqKey == "g" -> {
                        if (filtered.isNotEmpty()) appState.selectedEntry.value = filtered.first().first; true
                    }
                    event.isKey(Key.Return) && !isSearchFocused -> { appState.focusedPanel.value = 1; true }
                    event.isKey("f") && !isSearchFocused -> { appState.filterState.showModal.value = true; true }
                    else -> false
                }
            }
        return listComponent.modal(filterPanel.build(), appState.filterState.showModal)
    }

    fun free() {
        hScrollOffset.free()
        leftSubFocus.free()
        searchQuery.free()
    }

    private fun getFilteredEntries(): List<Pair<Int, har.Entry>> {
        val fs = appState.filterState
        val pat = searchQuery.value.trim()
        val urlFiltered = if (pat.isEmpty()) {
            appState.entries.mapIndexed { i, e -> i to e }
        } else try {
            val re = Regex(pat, RegexOption.IGNORE_CASE)
            appState.entries.mapIndexed { i, e -> i to e }.filter { (_, e) -> re.containsMatchIn(e.request.url) }
        } catch (_: Exception) {
            appState.entries.mapIndexed { i, e -> i to e }
        }
        return urlFiltered
            .filter { (_, e) -> fs.methodStates[e.request.method.uppercase()]?.value != false }
            .filter { (_, e) -> fs.typeStates[resourceType(e.response.content.mimeType)]?.value != false }
    }

    private fun buildRequestListContent(subElem: Element): Element {
        subElem.destroy()
        val maxMethodLen = appState.entries.maxOf { it.request.method.length }
        val isFocused = appState.focusedPanel.value == 0
        val isSearchFocused = leftSubFocus.value == 1
        val filtered = getFilteredEntries()

        if (filtered.none { it.first == appState.selectedEntry.value } && filtered.isNotEmpty()) {
            appState.selectedEntry.value = filtered[0].first
        }

        val scroll = hScrollOffset.value
        val listItems = filtered.map { (origIdx, entry) ->
            val method = entry.request.method.uppercase()
            val color = methodColor(method)
            val isSelected = origIdx == appState.selectedEntry.value
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

        val filterKeyColor = when {
            !isFocused -> Color.GrayDark
            appState.filterState.isActive -> Color.Yellow
            else -> Color.Default
        }
        val fs = appState.filterState
        val activeCount = fs.methodStates.values.count { !it.value } + fs.typeStates.values.count { !it.value }
        val filterElem = hbox(
            text(" [").color(filterKeyColor),
            text("f").bold().color(filterKeyColor),
            text("]ilter").color(filterKeyColor),
            if (activeCount > 0) text("  $activeCount active").color(filterKeyColor) else text(""),
        )

        val listElem = when {
            filtered.isEmpty() && (searchQuery.value.isNotBlank() || appState.filterState.isActive) ->
                vbox(text("  no matches").dim()).flex()
            else ->
                vbox(*listItems.toTypedArray())
                    .vscrollIndicator()
                    .yframe()
                    .flex()
        }

        val maxUrlLen = filtered.maxOfOrNull { (_, e) -> e.request.url.length } ?: 0
        val visibleW = maxOf(1, appState.leftSize.value - 2)

        return vbox(
            searchElem,
            filterElem,
            listElem,
            hScrollBar(hScrollOffset.value, maxUrlLen, visibleW),
        ).flex()
            .window(run {
                val labelColor = if (appState.focusedPanel.value == 0) Color.CyanLight else Color.GrayDark
                hbox(text(" [ "), text("r").underlined().bold().color(Color.GreenLight), text("equests").bold().color(labelColor), text(" ] "))
            })
            .let { if (appState.focusedPanel.value != 0) it.color(Color.GrayDark) else it }
    }
}
