import nl.ncaj.*

enum class TokenType { KEY, STRING, NUMBER, BOOLEAN, NULL, PUNCTUATION, WHITESPACE }

data class StyledSpan(val text: String, val type: TokenType)

fun isJson(mimeType: String) = mimeType.contains("json", ignoreCase = true)

fun colorFor(type: TokenType): Color = when (type) {
    TokenType.KEY -> Color.CyanLight
    TokenType.STRING -> Color.GreenLight
    TokenType.NUMBER -> Color.YellowLight
    TokenType.BOOLEAN -> Color.BlueLight
    TokenType.NULL -> Color.RedLight
    TokenType.PUNCTUATION -> Color.White
    TokenType.WHITESPACE -> Color.Default
}

fun renderHighlightedLine(spans: List<StyledSpan>): Element =
    if (spans.isEmpty()) text("")
    else hbox(*spans.map { text(it.text).color(colorFor(it.type)) }.toTypedArray())

fun clipSpans(spans: List<StyledSpan>, scrollX: Int): List<StyledSpan> {
    if (scrollX <= 0) return spans
    var offset = 0
    val result = mutableListOf<StyledSpan>()
    for (span in spans) {
        val end = offset + span.text.length
        when {
            end <= scrollX -> Unit
            offset >= scrollX -> result.add(span)
            else -> result.add(StyledSpan(span.text.substring(scrollX - offset), span.type))
        }
        offset = end
    }
    return result
}

private fun tokenizeJson(json: String): List<StyledSpan> {
    val result = mutableListOf<StyledSpan>()
    var i = 0
    val n = json.length
    val contextStack = ArrayDeque<Boolean>() // true = object, false = array
    var expectingKey = false

    while (i < n) {
        when (val c = json[i]) {
            '"' -> {
                val type = if (expectingKey) TokenType.KEY else TokenType.STRING
                val sb = StringBuilder().append(c)
                i++
                var escaped = false
                while (i < n) {
                    val sc = json[i]; i++
                    sb.append(sc)
                    if (escaped) escaped = false
                    else if (sc == '\\') escaped = true
                    else if (sc == '"') break
                }
                result.add(StyledSpan(sb.toString(), type))
            }
            '{' -> { contextStack.addLast(true); expectingKey = true; result.add(StyledSpan("{", TokenType.PUNCTUATION)); i++ }
            '}' -> { contextStack.removeLastOrNull(); expectingKey = false; result.add(StyledSpan("}", TokenType.PUNCTUATION)); i++ }
            '[' -> { contextStack.addLast(false); expectingKey = false; result.add(StyledSpan("[", TokenType.PUNCTUATION)); i++ }
            ']' -> { contextStack.removeLastOrNull(); expectingKey = false; result.add(StyledSpan("]", TokenType.PUNCTUATION)); i++ }
            ':' -> { expectingKey = false; result.add(StyledSpan(":", TokenType.PUNCTUATION)); i++ }
            ',' -> { expectingKey = contextStack.lastOrNull() == true; result.add(StyledSpan(",", TokenType.PUNCTUATION)); i++ }
            ' ', '\t', '\r', '\n' -> {
                val sb = StringBuilder()
                while (i < n && json[i] in " \t\r\n") sb.append(json[i++])
                result.add(StyledSpan(sb.toString(), TokenType.WHITESPACE))
            }
            '-', in '0'..'9' -> {
                val sb = StringBuilder()
                while (i < n && (json[i].isDigit() || json[i] in ".-+eE")) sb.append(json[i++])
                result.add(StyledSpan(sb.toString(), TokenType.NUMBER))
            }
            else -> if (c.isLetter()) {
                val sb = StringBuilder()
                while (i < n && json[i].isLetter()) sb.append(json[i++])
                val word = sb.toString()
                result.add(StyledSpan(word, when (word) {
                    "true", "false" -> TokenType.BOOLEAN
                    "null" -> TokenType.NULL
                    else -> TokenType.WHITESPACE
                }))
            } else {
                result.add(StyledSpan(c.toString(), TokenType.PUNCTUATION)); i++
            }
        }
    }
    return result
}

fun tokenizeJsonLines(json: String): List<List<StyledSpan>> {
    val lines = mutableListOf(mutableListOf<StyledSpan>())
    for (span in tokenizeJson(json)) {
        val parts = span.text.split('\n')
        parts.forEachIndexed { idx, part ->
            if (part.isNotEmpty()) lines.last().add(StyledSpan(part, span.type))
            if (idx < parts.lastIndex) lines.add(mutableListOf())
        }
    }
    return lines
}
