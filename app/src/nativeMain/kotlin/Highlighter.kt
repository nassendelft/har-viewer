import nl.ncaj.*

data class StyledSpan(val text: String, val color: Color)

interface Highlighter {
    fun accepts(mimeType: String): Boolean
    fun tokenizeLines(text: String): List<List<StyledSpan>>
}

private val highlighters: List<Highlighter> = listOf(
    JsonHighlighter, FormHighlighter, MarkupHighlighter, JsHighlighter
)

fun highlighterFor(mimeType: String): Highlighter? =
    highlighters.firstOrNull { it.accepts(mimeType) }

fun spansToLines(spans: List<StyledSpan>): List<List<StyledSpan>> {
    val lines = mutableListOf(mutableListOf<StyledSpan>())
    for (span in spans) {
        val parts = span.text.split('\n')
        parts.forEachIndexed { idx, part ->
            if (part.isNotEmpty()) lines.last().add(StyledSpan(part, span.color))
            if (idx < parts.lastIndex) lines.add(mutableListOf())
        }
    }
    return lines
}

fun renderHighlightedLine(spans: List<StyledSpan>): Element =
    if (spans.isEmpty()) text("")
    else hbox(*spans.map { text(it.text).color(it.color) }.toTypedArray())

fun clipSpans(spans: List<StyledSpan>, scrollX: Int, maxWidth: Int = Int.MAX_VALUE): List<StyledSpan> {
    val rightEdge = if (maxWidth == Int.MAX_VALUE) Int.MAX_VALUE else scrollX + maxWidth
    if (scrollX <= 0 && rightEdge == Int.MAX_VALUE) return spans
    var offset = 0
    val result = mutableListOf<StyledSpan>()
    for (span in spans) {
        val end = offset + span.text.length
        if (rightEdge != Int.MAX_VALUE && offset >= rightEdge) break
        val visStart = maxOf(offset, scrollX)
        val visEnd = if (rightEdge == Int.MAX_VALUE) end else minOf(end, rightEdge)
        if (visStart < visEnd) {
            val clipped = span.text.substring(visStart - offset, visEnd - offset)
            if (clipped.isNotEmpty()) result.add(StyledSpan(clipped, span.color))
        }
        offset = end
    }
    return result
}
