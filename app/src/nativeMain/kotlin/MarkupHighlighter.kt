import nl.ncaj.*

object MarkupHighlighter : Highlighter {
    override fun accepts(mimeType: String) =
        mimeType.contains("html", ignoreCase = true) ||
        mimeType.contains("xml", ignoreCase = true)

    override fun tokenizeLines(text: String) = spansToLines(tokenize(text))

    private enum class State {
        TEXT, TAG_START, TAG_NAME, CLOSE_TAG_NAME, ATTR_GAP, ATTR_NAME, ATTR_EQ, ATTR_VALUE,
        COMMENT, CDATA, PI, BANG
    }

    private fun tokenize(src: String): List<StyledSpan> {
        val result = mutableListOf<StyledSpan>()
        var i = 0
        val n = src.length
        var state = State.TEXT
        val buf = StringBuilder()
        var quoteChar = '"'

        fun flush(color: Color) {
            if (buf.isNotEmpty()) { result.add(StyledSpan(buf.toString(), color)); buf.clear() }
        }
        fun emit(text: String, color: Color) = result.add(StyledSpan(text, color))

        while (i < n) {
            val c = src[i]
            when (state) {
                State.TEXT -> {
                    if (c == '<') {
                        flush(Color.Default)
                        i++
                        state = State.TAG_START
                    } else {
                        buf.append(c); i++
                    }
                }
                State.TAG_START -> {
                    when {
                        c == '/' -> { emit("</", Color.White); i++; state = State.CLOSE_TAG_NAME }
                        c == '!' && src.startsWith("--", i + 1) -> {
                            emit("<!--", Color.GrayLight); i += 3; state = State.COMMENT
                        }
                        c == '!' && src.startsWith("[CDATA[", i + 1) -> {
                            emit("<![CDATA[", Color.GrayLight); i += 8; state = State.CDATA
                        }
                        c == '!' -> { emit("<!", Color.GrayLight); i++; state = State.BANG }
                        c == '?' -> { emit("<?", Color.GrayLight); i++; state = State.PI }
                        else -> { emit("<", Color.White); state = State.TAG_NAME }
                    }
                }
                State.TAG_NAME -> {
                    if (c.isLetterOrDigit() || c == '-' || c == '_' || c == ':' || c == '.') {
                        buf.append(c); i++
                    } else {
                        flush(Color.CyanLight); state = State.ATTR_GAP
                    }
                }
                State.CLOSE_TAG_NAME -> {
                    if (c.isLetterOrDigit() || c == '-' || c == '_' || c == ':' || c == '.') {
                        buf.append(c); i++
                    } else {
                        flush(Color.CyanLight)
                        if (c == '>') { emit(">", Color.White); i++ }
                        state = State.TEXT
                    }
                }
                State.ATTR_GAP -> {
                    when {
                        c == '>' -> { flush(Color.Default); emit(">", Color.White); i++; state = State.TEXT }
                        c == '/' && i + 1 < n && src[i + 1] == '>' -> {
                            flush(Color.Default); emit("/>", Color.White); i += 2; state = State.TEXT
                        }
                        c.isWhitespace() -> { buf.append(c); i++ }
                        else -> { flush(Color.Default); state = State.ATTR_NAME }
                    }
                }
                State.ATTR_NAME -> {
                    when {
                        c == '=' -> { flush(Color.YellowLight); state = State.ATTR_EQ }
                        c == '>' -> { flush(Color.YellowLight); emit(">", Color.White); i++; state = State.TEXT }
                        c.isWhitespace() -> { flush(Color.YellowLight); buf.append(c); i++; state = State.ATTR_GAP }
                        else -> { buf.append(c); i++ }
                    }
                }
                State.ATTR_EQ -> {
                    emit("=", Color.White); i++
                    state = State.ATTR_VALUE
                    if (i < n && (src[i] == '"' || src[i] == '\'')) {
                        quoteChar = src[i]; buf.append(src[i]); i++
                    }
                }
                State.ATTR_VALUE -> {
                    if (c == quoteChar) {
                        buf.append(c); i++; flush(Color.GreenLight); state = State.ATTR_GAP
                    } else {
                        buf.append(c); i++
                    }
                }
                State.COMMENT -> {
                    if (src.startsWith("-->", i)) {
                        flush(Color.GrayLight); emit("-->", Color.GrayLight); i += 3; state = State.TEXT
                    } else {
                        buf.append(c); i++
                    }
                }
                State.CDATA -> {
                    if (src.startsWith("]]>", i)) {
                        flush(Color.GrayLight); emit("]]>", Color.GrayLight); i += 3; state = State.TEXT
                    } else {
                        buf.append(c); i++
                    }
                }
                State.PI -> {
                    if (src.startsWith("?>", i)) {
                        flush(Color.GrayLight); emit("?>", Color.GrayLight); i += 2; state = State.TEXT
                    } else {
                        buf.append(c); i++
                    }
                }
                State.BANG -> {
                    if (c == '>') { flush(Color.GrayLight); emit(">", Color.GrayLight); i++; state = State.TEXT }
                    else { buf.append(c); i++ }
                }
            }
        }
        flush(Color.Default)
        return result
    }
}
