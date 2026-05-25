import nl.ncaj.*

object FormHighlighter : Highlighter {
    override fun accepts(mimeType: String) =
        mimeType.contains("x-www-form-urlencoded", ignoreCase = true)

    override fun tokenizeLines(text: String) = spansToLines(tokenize(text))

    private fun tokenize(text: String): List<StyledSpan> {
        val result = mutableListOf<StyledSpan>()
        var i = 0
        val n = text.length
        var inValue = false

        while (i < n) {
            when {
                text[i] == '=' && !inValue -> {
                    inValue = true
                    result.add(StyledSpan("=", Color.White))
                    i++
                }
                text[i] == '&' -> {
                    inValue = false
                    result.add(StyledSpan("&", Color.White))
                    i++
                }
                else -> {
                    val color = if (inValue) Color.GreenLight else Color.CyanLight
                    val sb = StringBuilder()
                    while (i < n && text[i] != '=' && text[i] != '&') sb.append(text[i++])
                    if (sb.isNotEmpty()) result.add(StyledSpan(sb.toString(), color))
                }
            }
        }
        return result
    }
}
