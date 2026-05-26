import kotlinx.coroutines.*
import nl.ncaj.*

internal class BodyTab(
    private val appState: AppState,
    private val bgScope: CoroutineScope,
    private val requestFrame: () -> Unit,
) {
    private val scrollX = IntState(0)
    private val scrollY = IntState(0)
    private val prettify = BoolState(false)
    private var lastContent = ""
    private var lastPrettify = false
    var isJson = false
        private set
    var lineCount = 0
        private set
    var maxLineWidth = 0
        private set
    private var highlightedLines: List<List<StyledSpan>>? = null
    private var pendingLines: List<List<StyledSpan>>? = null
    private var tokenizingJob: Job? = null

    fun build(): Component = renderer { render() }

    fun handleScrollEvent(event: FtxUIEvent, prevKey: String, contentHeight: Int): Boolean {
        when {
            event.isKey(Key.ArrowLeft) || event.isKey("h") -> {
                scrollX.value = maxOf(0, scrollX.value - 4); return true
            }
            event.isKey(Key.ArrowRight) || event.isKey("l") -> {
                val panelW = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2 - 1)
                val maxScrollX = maxOf(0, maxLineWidth - panelW)
                scrollX.value = minOf(maxScrollX, scrollX.value + 4); return true
            }
            event.isKey("p") && isJson -> {
                prettify.value = !prettify.value; return true
            }
        }
        return handleScrollEvents(event, prevKey, scrollY, lineCount, contentHeight)
    }

    fun free() {
        scrollX.free()
        scrollY.free()
        prettify.free()
    }

    private fun render(): Element {
        val entry = appState.entries[appState.selectedEntry.value]
        val content = entry.response.content
        val bodyText = (content.text ?: "(no body)").replace("\t", "    ")
        val mimeType = content.mimeType
        val isJsonContent = JsonHighlighter.accepts(mimeType)
        isJson = isJsonContent
        if (bodyText != lastContent) prettify.value = false
        if (bodyText != lastContent || prettify.value != lastPrettify) {
            lastContent = bodyText
            lastPrettify = prettify.value
            scrollX.value = 0
            scrollY.value = 0
            highlightedLines = null
            tokenizingJob?.cancel()
            tokenizingJob = null
            pendingLines = null
        }
        val displayText = if (prettify.value && isJsonContent) JsonHighlighter.prettyPrint(bodyText) else bodyText
        val bodyHighlighter = highlighterFor(mimeType)
        pendingLines?.let {
            highlightedLines = it
            pendingLines = null
            tokenizingJob = null
        }
        if (bodyHighlighter != null && highlightedLines == null && tokenizingJob == null) {
            tokenizingJob = bgScope.launch {
                pendingLines = bodyHighlighter.tokenizeLines(displayText)
                requestFrame()
            }
        }
        val lines = displayText.lines()
        lineCount = lines.size
        maxLineWidth = lines.maxOfOrNull { it.length } ?: 0
        val panelWidth = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2 - 1)
        val bodyH = maxOf(1, Terminal.size().dimy - 8)
        val visibleLines = lines.drop(scrollY.value).take(bodyH)
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
                if (mimeType.isNotEmpty()) hbox(text("  "), text(mimeType).color(black).underlined()) else text(""),
                text(sizeInfo).color(black),
                text("  ${lines.size} lines").color(black),
                if (isJsonContent) text("  [p] pretty").let {
                    if (prettify.value) it.color(yellow) else it.color(black).dim()
                } else text(""),
                filler(),
            ).bgcolor(beige),
            separatorEmpty(),
            hbox(
                vbox(*visibleLines.mapIndexed { idx, line ->
                    val highlighted = highlightedLines
                    if (bodyHighlighter != null && highlighted != null) {
                        val spans = highlighted.getOrElse(scrollY.value + idx) { emptyList() }
                        renderHighlightedLine(clipSpans(spans, scrollX.value, panelWidth))
                    } else {
                        val displayed = when {
                            scrollX.value <= 0 -> line
                            scrollX.value >= line.length -> ""
                            else -> line.substring(scrollX.value)
                        }
                        text(displayed)
                    }
                }.toTypedArray()).flex(),
                vScrollBar(scrollY.value, lineCount, bodyH),
            ).flex(),
            hScrollBar(scrollX.value, maxLineWidth, panelWidth),
        ).flex()
    }
}
