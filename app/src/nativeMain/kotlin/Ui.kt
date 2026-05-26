import har.Cookie
import har.Param
import nl.ncaj.*

internal fun methodColor(method: String): Color = when (method.uppercase()) {
    "GET" -> Color.GreenLight
    "POST" -> yellow
    "PUT" -> Color.BlueLight
    "DELETE" -> Color.RedLight
    "PATCH" -> Color.CyanLight
    "HEAD", "OPTIONS" -> Color.MagentaLight
    else -> Color.GrayLight
}

internal val beige = Color.rgb(0xEBu, 0xE2u, 0xC3u)
internal val yellow = Color.rgb(0xFFu, 0xB7u, 0x00u)
internal val black = Color.rgb(0u, 0u, 0u)

internal fun cookieAnnotation(c: Cookie): String? = listOfNotNull(
    c.path?.let { "path=$it" },
    c.domain?.let { "domain=$it" },
    c.expires?.let { "expires=$it" },
    if (c.httpOnly == true) "HttpOnly" else null,
    if (c.secure == true) "Secure" else null,
).joinToString("  ").ifEmpty { null }

internal fun paramAnnotation(p: Param): String? = listOfNotNull(
    p.fileName?.let { "file=$it" },
    p.contentType?.let { "type=$it" },
).joinToString("  ").ifEmpty { null }

internal fun keyValueRows(
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

internal fun vScrollBar(scrollY: Int, total: Int, visible: Int): Element {
    if (total <= visible) return vbox(*(0 until maxOf(1, visible)).map { text(" ") }.toTypedArray())
    val thumbH = maxOf(1, visible * visible / total)
    val thumbY = ((scrollY.toLong() * maxOf(0, visible - thumbH)) / maxOf(1, total - visible)).toInt()
    return vbox(*(0 until visible).map { i ->
        if (i in thumbY until thumbY + thumbH) text("▐").color(Color.GrayLight)
        else text("▕").dim()
    }.toTypedArray())
}

internal fun hScrollBar(scrollX: Int, total: Int, visible: Int): Element {
    if (total <= visible) return hbox(*(0 until maxOf(1, visible)).map { text(" ") }.toTypedArray())
    val thumbW = maxOf(1, visible * visible / total)
    val thumbX = ((scrollX.toLong() * maxOf(0, visible - thumbW)) / maxOf(1, total - visible)).toInt()
    return hbox(*(0 until visible).map { i ->
        if (i in thumbX until thumbX + thumbW) text("▁").color(Color.GrayLight)
        else text("▁").dim()
    }.toTypedArray())
}

internal fun renderTabBar(tabSelected: Int, focusedPanel: Int, tabLabels: List<String>): Element {
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
