import har.HarHeader
import har.parseHar
import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

private fun methodColor(method: String): Color = when (method.uppercase()) {
    "GET" -> Color.GreenLight
    "POST" -> Color.YellowLight
    "PUT" -> Color.BlueLight
    "DELETE" -> Color.RedLight
    "PATCH" -> Color.CyanLight
    "HEAD", "OPTIONS" -> Color.MagentaLight
    else -> Color.GrayLight
}

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

    val maxMethodLen = entries.maxOf { it.request.method.length }
    val entryLabels = entries.map { "${it.request.method}  ${it.request.url}" }

    val requestListMenu = menu(entryLabels, selectedEntry)
    val requestListRenderer = requestListMenu.decorateRender { menuElem ->
        menuElem.destroy()
        val scroll = hScrollOffset.value
        val coloredList = vbox(*entries.mapIndexed { i, entry ->
            val method = entry.request.method.uppercase()
            val color = methodColor(method)
            val isSelected = i == selectedEntry.value
            val url = entry.request.url
            val prefix = if (scroll > 0) "…" else " "
            val displayUrl = if (scroll > 0) url.drop(scroll) else url
            val methodElem = text(" ${method.padEnd(maxMethodLen)} ").bold()
                .let { if (isSelected) it.bgcolor(color).color(Color.Default) else it.color(color) }
            val urlElem = text("$prefix$displayUrl${" ".repeat(500)}")
                .let { if (isSelected) it.bgcolor(Color.Blue).color(Color.White) else it }
                .xflex()
            hbox(methodElem, urlElem)
                .let { if (isSelected) it.focus() else it }
        }.toTypedArray())
        coloredList
            .vscrollIndicator()
            .yframe()
            .flex()
            .window(text("Requests").bold().color(Color.CyanLight))
    }.catchEvent { event ->
        val maxOffset = entries[selectedEntry.value].request.url.length
        when (event.debugString) {
            "Event::ArrowRight" -> {
                hScrollOffset.value = minOf(hScrollOffset.value + 5, maxOffset); true
            }

            "Event::ArrowLeft" -> {
                hScrollOffset.value = maxOf(hScrollOffset.value - 5, 0); true
            }

            "Event::ArrowUp", "Event::ArrowDown" -> {
                hScrollOffset.value = 0; false
            }

            else -> false
        }
    }

    val tabLabels = listOf("Req Headers", "Resp Headers", "Body", "Timings")
    val tabMenu = menuHorizontalAnimated(tabLabels, tabSelected)
    val tabContainer = tab(tabSelected)

    fun headerTable(headers: List<HarHeader>): Element {
        if (headers.isEmpty()) return text("(none)").dim()
        val nameWidth = headers.maxOf { it.name.length }
        val chunkSize = 60
        val rows = mutableListOf<Element>()
        for ((i, header) in headers.withIndex()) {
            val chunks = header.value.chunked(chunkSize)
            chunks.forEachIndexed { j, chunk ->
                val nameCell = if (j == 0) text(header.name.padEnd(nameWidth)).bold().color(Color.CyanLight)
                else text(" ".repeat(nameWidth))
                rows.add(hbox(nameCell, text(" │ ").dim(), text(chunk).color(Color.GrayLight).xflex()))
            }
            if (i < headers.lastIndex) rows.add(separator())
        }
        return vbox(*rows.toTypedArray()).vscrollIndicator().yframe().flex()
    }

    // Tab 0: Request headers
    tabContainer.add(renderer {
        val entry = entries[selectedEntry.value]
        headerTable(entry.request.headers)
    })

    // Tab 1: Response headers
    tabContainer.add(renderer {
        val entry = entries[selectedEntry.value]
        headerTable(entry.response.headers)
    })

    // Tab 2: Response body
    tabContainer.add(renderer {
        val entry = entries[selectedEntry.value]
        val lines = (entry.response.content.text ?: "(no body)").lines().take(1000)
        vbox(*lines.map { text(it) }.toTypedArray()).vscrollIndicator().hscrollIndicator().frame().flex()
    })

    // Tab 3: Timings
    tabContainer.add(renderer {
        val t = entries[selectedEntry.value].timings
        fun row(label: String, ms: Double): Element =
            if (ms < 0) hbox(text(label).color(Color.GrayDark), text("n/a").dim())
            else hbox(
                text(label).color(Color.CyanLight),
                text("$ms ms").color(Color.YellowLight)
            )
        vbox(
            row("Blocked: ", t.blocked),
            row("DNS:     ", t.dns),
            row("Connect: ", t.connect),
            row("SSL:     ", t.ssl),
            row("Send:    ", t.send),
            row("Wait:    ", t.wait),
            row("Receive: ", t.receive),
        ).frame().flex()
    })

    val detailContainer = vertical(tabMenu, tabContainer)
    val detailRenderer = renderer(detailContainer) {
        val entry = entries[selectedEntry.value]
        val method = entry.request.method.uppercase()
        val mColor = methodColor(method)
        vbox(
            hbox(
                text(" $method ").bold().bgcolor(mColor).color(Color.Default),
                text("  ${entry.request.url}"),
            ),
            separator(),
            tabMenu.render(),
            tabContainer.render().flex(),
        ).flex().window(text("Details").bold().color(Color.CyanLight))
    }

    val mainContainer = resizableSplit(
        requestListRenderer,
        detailRenderer,
        leftSize,
        Direction.Left,
        separator = { separatorEmpty() }
    )

    val app = FtxUIApp.fullscreen()
    app.loop(mainContainer)

    app.destroy()
    selectedEntry.free()
    tabSelected.free()
    leftSize.free()
    hScrollOffset.free()
}