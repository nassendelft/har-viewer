package highlight

import nl.ncaj.*

object JsHighlighter : Highlighter {
    override fun accepts(mimeType: String) =
        mimeType.contains("javascript", ignoreCase = true)

    override fun tokenizeLines(text: String) = spansToLines(tokenize(text))

    private val keywords = setOf(
        "break", "case", "catch", "class", "const", "continue", "debugger", "default",
        "delete", "do", "else", "export", "extends", "false", "finally", "for", "from",
        "function", "if", "import", "in", "instanceof", "let", "new", "null", "of",
        "return", "static", "super", "switch", "this", "throw", "true", "try", "typeof",
        "undefined", "var", "void", "while", "async", "await"
    )

    private fun tokenize(src: String): List<StyledSpan> {
        val result = mutableListOf<StyledSpan>()
        var i = 0
        val n = src.length

        fun emit(text: String, color: Color) = result.add(StyledSpan(text, color))

        while (i < n) {
            val c = src[i]
            when {
                // line comment
                c == '/' && i + 1 < n && src[i + 1] == '/' -> {
                    val sb = StringBuilder()
                    while (i < n && src[i] != '\n') sb.append(src[i++])
                    emit(sb.toString(), Color.GrayLight)
                }
                // block comment
                c == '/' && i + 1 < n && src[i + 1] == '*' -> {
                    val sb = StringBuilder("/*"); i += 2
                    while (i < n) {
                        if (src[i] == '*' && i + 1 < n && src[i + 1] == '/') {
                            sb.append("*/"); i += 2; break
                        }
                        sb.append(src[i++])
                    }
                    emit(sb.toString(), Color.GrayLight)
                }
                // string: single-quoted, double-quoted, template literal
                c == '\'' || c == '"' || c == '`' -> {
                    val quote = c
                    val sb = StringBuilder().append(c); i++
                    while (i < n) {
                        val sc = src[i]; i++
                        sb.append(sc)
                        if (sc == '\\' && i < n) { sb.append(src[i++]) }
                        else if (sc == quote) break
                    }
                    emit(sb.toString(), Color.GreenLight)
                }
                // number
                c.isDigit() || (c == '.' && i + 1 < n && src[i + 1].isDigit()) -> {
                    val sb = StringBuilder()
                    while (i < n && (src[i].isLetterOrDigit() || src[i] in "._+eExX")) sb.append(src[i++])
                    emit(sb.toString(), Color.YellowLight)
                }
                // identifier or keyword
                c.isLetter() || c == '_' || c == '$' -> {
                    val sb = StringBuilder()
                    while (i < n && (src[i].isLetterOrDigit() || src[i] == '_' || src[i] == '$')) sb.append(src[i++])
                    val word = sb.toString()
                    emit(word, if (word in keywords) Color.BlueLight else Color.Default)
                }
                // whitespace — preserve as-is (Default color, not emitted as punctuation)
                c.isWhitespace() -> {
                    val sb = StringBuilder()
                    while (i < n && src[i].isWhitespace()) sb.append(src[i++])
                    emit(sb.toString(), Color.Default)
                }
                // everything else: operators and punctuation
                else -> { emit(c.toString(), Color.White); i++ }
            }
        }
        return result
    }
}
