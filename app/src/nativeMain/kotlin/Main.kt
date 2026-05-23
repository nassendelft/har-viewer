import har.*
import kotlinx.cinterop.ExperimentalForeignApi
import nl.ncaj.*

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    val path = args.firstOrNull() ?: run { println("Usage: har-viewer <file.har>"); return }
    val har = parseHar(path)
    val entries = har.log.entries
    if (entries.isEmpty()) { println("No entries found in HAR file."); return }

    val selectedEntry = IntState(0)
    val tabSelected = IntState(0)
    val leftSize = IntState(50)

    val entryLabels = entries.map { "${it.request.method}  ${it.request.url}" }
    val maxLabelWidth = entryLabels.maxOf { it.length } + 2
    val requestListMenu = menu(entryLabels, selectedEntry)
    val requestListRenderer = renderer(requestListMenu) {
        requestListMenu.render()
            .size(WidthOrHeight.Width, Constraint.GreaterThan, maxLabelWidth)
            .vscrollIndicator()
            .hscrollIndicator()
            .frame()
            .flex()
            .window(text("Requests"))
    }

    val tabLabels = listOf("Req Headers", "Resp Headers", "Body", "Timings")
    val tabMenu = menuHorizontal(tabLabels, tabSelected)
    val tabContainer = tab(tabSelected)

    fun headerRows(headers: List<HarHeader>): Element =
        if (headers.isEmpty()) text("(none)")
        else vbox(*headers.map { hbox(text(it.name).bold(), text(": "), text(it.value)) }.toTypedArray())

    // Tab 0: Request headers
    tabContainer.add(renderer {
        val entry = entries[selectedEntry.value]
        headerRows(entry.request.headers).vscrollIndicator().frame().flex()
    })

    // Tab 1: Response headers
    tabContainer.add(renderer {
        val entry = entries[selectedEntry.value]
        headerRows(entry.response.headers).vscrollIndicator().frame().flex()
    })

    // Tab 2: Response body
    tabContainer.add(renderer {
        val entry = entries[selectedEntry.value]
        val lines = (entry.response.content.text ?: "(no body)").lines().take(1000)
        vbox(*lines.map { text(it) }.toTypedArray()).vscrollIndicator().frame().flex()
    })

    // Tab 3: Timings
    tabContainer.add(renderer {
        val t = entries[selectedEntry.value].timings
        fun row(label: String, ms: Double): Element =
            if (ms < 0) hbox(text(label), text("n/a").dim())
            else hbox(text(label), text("$ms ms"))
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
        vbox(
            tabMenu.render(),
            separator(),
            tabContainer.render().flex(),
        ).flex().window(text("Details"))
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
}
