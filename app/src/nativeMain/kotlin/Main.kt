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
    val focusedPanel = IntState(0) // 0 = requests, 1 = details
    val bodyScrollX = IntState(0)
    val bodyScrollY = IntState(0)
    var bodyLastContent = ""

    val maxMethodLen = entries.maxOf { it.request.method.length }
    val entryLabels = entries.map { "${it.request.method}  ${it.request.url}" }

    val requestListMenu = menu(entryLabels, selectedEntry)
    val requestListRenderer = requestListMenu.decorateRender { menuElem ->
        menuElem.destroy()
        val scroll = hScrollOffset.value
        val isFocused = focusedPanel.value == 0
        vbox(*entries.mapIndexed { i, entry ->
            val method = entry.request.method.uppercase()
            val color = methodColor(method)
            val isSelected = i == selectedEntry.value
            val url = entry.request.url
            val prefix = if (scroll > 0) "…" else " "
            val displayUrl = if (scroll > 0) url.drop(scroll) else url
            val methodElem = text(" ${method.padEnd(maxMethodLen)} ").bold()
                .let { if (isSelected && isFocused) it.bgcolor(color).color(Color.Default) else it.color(color) }
            val urlElem = text("$prefix$displayUrl${" ".repeat(500)}")
                .let { if (isSelected && isFocused) it.bgcolor(Color.Blue).color(Color.White) else it }
                .xflex()
            hbox(methodElem, urlElem)
                .let { if (isSelected) it.focus() else it }
        }.toTypedArray())
            .vscrollIndicator()
            .yframe()
            .flex()
            .window(run {
                val keyColor = if (focusedPanel.value == 0) Color.GreenLight else Color.GrayDark
                val labelColor = if (focusedPanel.value == 0) Color.CyanLight else Color.GrayDark
                hbox(text(" [ "), text("R").underlined().bold().color(keyColor), text("equests").bold().color(labelColor), text(" ] "))
            })
            .let { if (focusedPanel.value != 0) it.color(Color.GrayDark) else it }
    }.catchEvent { event ->
        val maxOffset = entries[selectedEntry.value].request.url.length
        when {
            event.isKey(Key.ArrowRight) -> {
                hScrollOffset.value = minOf(hScrollOffset.value + 5, maxOffset); true
            }
            event.isKey(Key.ArrowLeft) -> {
                hScrollOffset.value = maxOf(hScrollOffset.value - 5, 0); true
            }
            event.isKey(Key.ArrowUp) || event.isKey(Key.ArrowDown) -> {
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
                rows.add(hbox(nameCell, text(" │ ").dim(), text(chunk).color(Color.GrayLight)))
            }
            if (i < headers.lastIndex) rows.add(separator())
        }
        return vbox(*rows.toTypedArray())
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

    // Tab 2: Response body — line-clipping driven by bodyScrollY/X state
    tabContainer.add(renderer {
        val content = entries[selectedEntry.value].response.content.text ?: "(no body)"
        if (content != bodyLastContent) {
            bodyLastContent = content
            bodyScrollX.value = 0
            bodyScrollY.value = 0
        }
        val lines = content.lines()
        val visibleLines = lines.drop(bodyScrollY.value)
        vbox(*visibleLines.map { line ->
            val displayed = when {
                bodyScrollX.value <= 0 -> line
                bodyScrollX.value >= line.length -> ""
                else -> line.substring(bodyScrollX.value)
            }
            text(displayed)
        }.toTypedArray()).flex()
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

    val detailRenderer = renderer(tabMenu) {
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
        ).flex().window(run {
            val keyColor = if (focusedPanel.value == 1) Color.GreenLight else Color.GrayDark
            val labelColor = if (focusedPanel.value == 1) Color.CyanLight else Color.GrayDark
            hbox(text(" [ "), text("D").underlined().bold().color(keyColor), text("etails").bold().color(labelColor), text(" ] "))
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
        val onDetails = focusedPanel.value == 1
        val onBody = onDetails && tabSelected.value == 2
        when {
            event.isKey("r") -> { focusedPanel.value = 0; true }
            event.isKey("d") -> { focusedPanel.value = 1; true }
            // Body scrolling — checked before tab switching so arrows work on body tab
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
            // Tab switching (left/right when in details panel and not on body tab)
            event.isKey(Key.ArrowLeft) && onDetails -> {
                tabSelected.value = maxOf(0, tabSelected.value - 1); true
            }
            event.isKey(Key.ArrowRight) && onDetails -> {
                tabSelected.value = minOf(tabLabels.size - 1, tabSelected.value + 1); true
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
}
