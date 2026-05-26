package highlight

import kotlinx.serialization.json.*
import nl.ncaj.*

private enum class JsonTokenType { KEY, STRING, NUMBER, BOOLEAN, NULL, PUNCTUATION, WHITESPACE }

private fun colorFor(type: JsonTokenType): Color = when (type) {
    JsonTokenType.KEY         -> Color.CyanLight
    JsonTokenType.STRING      -> Color.GreenLight
    JsonTokenType.NUMBER      -> Color.YellowLight
    JsonTokenType.BOOLEAN     -> Color.BlueLight
    JsonTokenType.NULL        -> Color.RedLight
    JsonTokenType.PUNCTUATION -> Color.White
    JsonTokenType.WHITESPACE  -> Color.Default
}

private val prettyJson = Json { prettyPrint = true }

object JsonHighlighter : Highlighter {
    override fun accepts(mimeType: String) = mimeType.contains("json", ignoreCase = true)

    override fun tokenizeLines(text: String) = spansToLines(tokenize(text))

    fun prettyPrint(text: String): String = try {
        val element = Json.parseToJsonElement(text)
        prettyJson.encodeToString(JsonElement.serializer(), element)
    } catch (_: Exception) {
        text
    }

    private fun tokenize(json: String): List<StyledSpan> {
        val result = mutableListOf<StyledSpan>()
        var i = 0
        val n = json.length
        val contextStack = ArrayDeque<Boolean>() // true = object, false = array
        var expectingKey = false

        fun emit(text: String, type: JsonTokenType) = result.add(StyledSpan(text, colorFor(type)))

        while (i < n) {
            when (val c = json[i]) {
                '"' -> {
                    val type = if (expectingKey) JsonTokenType.KEY else JsonTokenType.STRING
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
                    emit(sb.toString(), type)
                }
                '{' -> { contextStack.addLast(true); expectingKey = true; emit("{", JsonTokenType.PUNCTUATION); i++ }
                '}' -> { contextStack.removeLastOrNull(); expectingKey = false; emit("}", JsonTokenType.PUNCTUATION); i++ }
                '[' -> { contextStack.addLast(false); expectingKey = false; emit("[", JsonTokenType.PUNCTUATION); i++ }
                ']' -> { contextStack.removeLastOrNull(); expectingKey = false; emit("]", JsonTokenType.PUNCTUATION); i++ }
                ':' -> { expectingKey = false; emit(":", JsonTokenType.PUNCTUATION); i++ }
                ',' -> { expectingKey = contextStack.lastOrNull() == true; emit(",", JsonTokenType.PUNCTUATION); i++ }
                ' ', '\t', '\r', '\n' -> {
                    val sb = StringBuilder()
                    while (i < n && json[i] in " \t\r\n") sb.append(json[i++])
                    emit(sb.toString(), JsonTokenType.WHITESPACE)
                }
                '-', in '0'..'9' -> {
                    val sb = StringBuilder()
                    while (i < n && (json[i].isDigit() || json[i] in ".-+eE")) sb.append(json[i++])
                    emit(sb.toString(), JsonTokenType.NUMBER)
                }
                else -> if (c.isLetter()) {
                    val sb = StringBuilder()
                    while (i < n && json[i].isLetter()) sb.append(json[i++])
                    val word = sb.toString()
                    emit(word, when (word) {
                        "true", "false" -> JsonTokenType.BOOLEAN
                        "null" -> JsonTokenType.NULL
                        else -> JsonTokenType.WHITESPACE
                    })
                } else {
                    emit(c.toString(), JsonTokenType.PUNCTUATION); i++
                }
            }
        }
        return result
    }
}
