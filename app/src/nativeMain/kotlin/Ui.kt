import har.Cookie
import har.Param
import nl.ncaj.*

private const val DATA_URL_MAX = 64

internal fun displayUrl(url: String): String {
    if (!url.startsWith("data:")) return url
    val header = url.substringBefore(',')
    val preview = "$header,…"
    return if (preview.length <= DATA_URL_MAX) preview else "${url.take(DATA_URL_MAX)}…"
}

internal fun resourceType(mimeType: String): ResourceType {
    val base = mimeType.substringBefore(';').trim().lowercase()
    return when {
        base == "text/html" || base == "application/xhtml+xml" -> ResourceType.Document
        base == "application/javascript" || base == "text/javascript" || base == "application/x-javascript" -> ResourceType.Script
        base == "text/css" -> ResourceType.Stylesheet
        base.startsWith("image/") -> ResourceType.Image
        base.startsWith("audio/") || base.startsWith("video/") -> ResourceType.Media
        base.startsWith("font/") || base.startsWith("application/font-") ||
            base == "application/x-font-woff" || base == "application/vnd.ms-fontobject" -> ResourceType.Font
        base == "application/json" || base == "application/xml" || base == "text/xml" ||
            base == "application/x-www-form-urlencoded" || base.startsWith("multipart/") ||
            base == "text/csv" -> ResourceType.XHR
        else -> ResourceType.Other
    }
}

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
    val rows = mutableListOf<Element>()
    if (panelWidth > 0) {
        val keyWidth = maxOf(10, panelWidth / 4)
        val valueWidth = maxOf(20, panelWidth - keyWidth - 3)
        for ((i, pair) in pairs.withIndex()) {
            val (name, value) = pair
            val nameChunks = name.chunked(keyWidth)
            val valueChunks = value.chunked(valueWidth)
            val maxChunks = maxOf(nameChunks.size, valueChunks.size)
            for (j in 0 until maxChunks) {
                val namePadded = nameChunks.getOrElse(j) { "" }.padEnd(keyWidth)
                val valueChunk = valueChunks.getOrElse(j) { "" }
                val nameCell = text(namePadded).bold().color(Color.CyanLight)
                rows.add(hbox(nameCell, text(" │ ").dim(), text(valueChunk).color(beige)))
            }
            annotations.getOrNull(i)?.let { rows.add(hbox(text(" ".repeat(keyWidth + 3)), text(it).dim())) }
            if (i < pairs.lastIndex) rows.add(separator())
        }
    } else {
        val nameWidth = pairs.maxOf { it.first.length }
        for ((i, pair) in pairs.withIndex()) {
            val (name, value) = pair
            rows.add(hbox(text(name.padEnd(nameWidth)).bold().color(Color.CyanLight), text(" │ ").dim(), text(value).color(beige)))
            annotations.getOrNull(i)?.let { rows.add(hbox(text(" ".repeat(nameWidth + 3)), text(it).dim())) }
            if (i < pairs.lastIndex) rows.add(separator())
        }
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
            isActive -> hbox(
                text(" [ "),
                text("${i + 1}").bold().underlined().color(Color.GreenLight),
                text(" $label ").bold().color(if (detailsFocused) Color.CyanLight else Color.GrayDark),
                text(" ] "),
            )
            else -> hbox(
                text(" [ "),
                text("${i + 1}").underlined().color(Color.GrayDark),
                text(" $label ").color(Color.GrayDark),
                text(" ] "),
            )
        }
    }
    return hbox(*tabItems.toTypedArray())
}
