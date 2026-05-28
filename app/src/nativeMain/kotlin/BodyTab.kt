import har.Content
import highlight.*
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import nl.ncaj.*
import platform.posix.*
import stb_image.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val ESC = ""
private const val ST = "\\"

@OptIn(ExperimentalForeignApi::class)
internal class BodyTab(
    private val appState: AppState,
    private val bgScope: CoroutineScope,
    private val requestFrame: () -> Unit,
) {
    private val scrollX = IntState(0)
    private val scrollY = IntState(0)
    private val prettify = BoolState(false)
    private var lastContent = ""
    private var lastPrettify = false
    var isJson = false
        private set
    var lineCount = 0
        private set
    var maxLineWidth = 0
        private set
    private var highlightedLines: List<List<StyledSpan>>? = null
    private var pendingLines: List<List<StyledSpan>>? = null
    private var tokenizingJob: Job? = null

    private var lastImageEntryIdx = -1
    private var imageBytes: ByteArray? = null
    private var decodedImage: DecodedImage? = null
    private var pendingImage: DecodedImage? = null
    private var decodeJob: Job? = null
    private var kittyImageId = 0
    private var kittyUploaded = false
    val kittySupported = isKittySupported()

    private data class DecodedImage(val pixels: ByteArray, val width: Int, val height: Int)

    fun build(): Component = renderer { render() }

    fun handleScrollEvent(event: FtxUIEvent, prevKey: String, contentHeight: Int): Boolean {
        val entry = appState.entries[appState.selectedEntry.value]
        if (isImageContent(entry.response.content, entry.request.url)) {
            if (kittySupported) return false
            val image = decodedImage ?: return false
            val panelW = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2 - 1)
            val dstW = minOf(image.width, panelW)
            val scaleY = image.width.toDouble() / dstW
            val dstH = (image.height / scaleY).toInt()
            val termRows = (dstH + 1) / 2
            return handleScrollEvents(event, prevKey, scrollY, termRows, contentHeight)
        }
        when {
            event.isKey(Key.ArrowLeft) || event.isKey("h") -> {
                scrollX.value = maxOf(0, scrollX.value - 4); return true
            }
            event.isKey(Key.ArrowRight) || event.isKey("l") -> {
                val gutterW = lineCount.toString().length + 1
                val panelW = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2 - 1 - gutterW)
                val maxScrollX = maxOf(0, maxLineWidth - panelW)
                scrollX.value = minOf(maxScrollX, scrollX.value + 4); return true
            }
            event.isKey("p") && isJson -> {
                prettify.value = !prettify.value; return true
            }
        }
        return handleScrollEvents(event, prevKey, scrollY, lineCount, contentHeight)
    }

    fun deactivate() {
        if (kittySupported && kittyUploaded) {
            writeToStdout("${ESC}_Ga=d,d=i,q=2,i=$kittyImageId$ST")
            kittyUploaded = false
        }
    }

    fun free() {
        deactivate()
        scrollX.free()
        scrollY.free()
        prettify.free()
    }

    private fun render(): Element {
        val entry = appState.entries[appState.selectedEntry.value]
        val content = entry.response.content
        if (appState.selectedEntry.value != lastImageEntryIdx && kittyUploaded) deactivate()
        return if (isImageContent(content, entry.request.url)) renderImage(content)
        else renderText(content)
    }

    private fun renderImage(content: Content): Element {
        val currentIdx = appState.selectedEntry.value
        if (currentIdx != lastImageEntryIdx) {
            deactivate()
            lastImageEntryIdx = currentIdx
            scrollY.value = 0
            imageBytes = null
            decodedImage = null
            pendingImage = null
            decodeJob?.cancel()
            decodeJob = null
            val bytes = getImageBytes(content)
            imageBytes = bytes
            if (!kittySupported && bytes != null) {
                decodeJob = bgScope.launch {
                    pendingImage = decodeImageBytes(bytes)
                    requestFrame()
                }
            }
        }
        pendingImage?.let {
            decodedImage = it
            pendingImage = null
            decodeJob = null
        }
        val panelW = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2 - 1)
        val bodyH = maxOf(1, Terminal.size().dimy - 6)
        return if (kittySupported) renderKitty(imageBytes, content, panelW, bodyH)
        else {
            val image = decodedImage
            when {
                image != null -> renderHalfBlock(image, content, panelW, bodyH)
                decodeJob != null -> vbox(separatorEmpty(), renderImageHeader(content, null, null), separatorEmpty(), text("  Decoding image…").dim().flex()).flex()
                else -> vbox(separatorEmpty(), renderImageHeader(content, null, null), separatorEmpty(), text("  Unable to decode image").dim().flex()).flex()
            }
        }
    }

    private fun renderText(content: Content): Element {
        val bodyText = (content.text ?: "(no body)").replace("\t", "    ")
        val mimeType = content.mimeType
        val isJsonContent = JsonHighlighter.accepts(mimeType)
        isJson = isJsonContent
        if (bodyText != lastContent) prettify.value = false
        if (bodyText != lastContent || prettify.value != lastPrettify) {
            lastContent = bodyText
            lastPrettify = prettify.value
            scrollX.value = 0
            scrollY.value = 0
            highlightedLines = null
            tokenizingJob?.cancel()
            tokenizingJob = null
            pendingLines = null
        }
        val displayText = if (prettify.value && isJsonContent) JsonHighlighter.prettyPrint(bodyText) else bodyText
        val bodyHighlighter = highlighterFor(mimeType)
        pendingLines?.let {
            highlightedLines = it
            pendingLines = null
            tokenizingJob = null
        }
        if (bodyHighlighter != null && highlightedLines == null && tokenizingJob == null) {
            tokenizingJob = bgScope.launch {
                pendingLines = bodyHighlighter.tokenizeLines(displayText)
                requestFrame()
            }
        }
        val lines = displayText.lines()
        lineCount = lines.size
        maxLineWidth = lines.maxOfOrNull { it.length } ?: 0
        val lineNumberWidth = lineCount.toString().length
        val gutterWidth = lineNumberWidth + 1
        val panelWidth = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2 - 1 - gutterWidth)
        val bodyH = maxOf(1, Terminal.size().dimy - 6)
        val visibleLines = lines.drop(scrollY.value).take(bodyH)
        val sizeInfo = buildString {
            append("  ${content.size}B")
            val comp = content.compression
            if (comp != null && comp > 0) append(" (${comp}B saved)")
            val enc = content.encoding
            if (!enc.isNullOrEmpty()) append("  $enc")
        }
        return vbox(
            separatorEmpty(),
            hbox(
                text(" Response Body").bold().color(black),
                if (mimeType.isNotEmpty()) hbox(text("  "), text(mimeType).color(black).underlined()) else text(""),
                text(sizeInfo).color(black),
                text("  ${lines.size} lines").color(black),
                if (isJsonContent) text("  [p] pretty").let {
                    if (prettify.value) it.color(yellow) else it.color(black).dim()
                } else text(""),
                filler(),
            ).bgcolor(beige),
            separatorEmpty(),
            hbox(
                vbox(*List(visibleLines.size) { idx ->
                    text((scrollY.value + idx + 1).toString().padStart(lineNumberWidth) + " ").dim()
                }.toTypedArray()),
                vbox(*visibleLines.mapIndexed { idx, line ->
                    val highlighted = highlightedLines
                    if (bodyHighlighter != null && highlighted != null) {
                        val spans = highlighted.getOrElse(scrollY.value + idx) { emptyList() }
                        renderHighlightedLine(clipSpans(spans, scrollX.value, panelWidth))
                    } else {
                        val displayed = when {
                            scrollX.value <= 0 -> line
                            scrollX.value >= line.length -> ""
                            else -> line.substring(scrollX.value)
                        }
                        text(displayed)
                    }
                }.toTypedArray()).flex(),
                vScrollBar(scrollY.value, lineCount, bodyH),
            ).flex(),
            hScrollBar(scrollX.value, maxLineWidth, panelWidth),
        ).flex()
    }

    private fun renderKitty(bytes: ByteArray?, content: Content, panelW: Int, bodyH: Int): Element {
        val header = renderImageHeader(content, null, null)
        if (bytes == null) {
            return vbox(separatorEmpty(), header, separatorEmpty(), text("  Unable to decode image").dim().flex()).flex()
        }
        if (!kittyUploaded) {
            kittyImageId = (kittyImageId % 9999) + 1
            uploadKittyImage(bytes, kittyImageId)
            kittyUploaded = true
        }
        val imageRow = 6
        val imageCol = appState.leftSize.value + 2
        writeToStdout("${ESC}[${imageRow};${imageCol}H${ESC}_Ga=p,q=2,i=$kittyImageId,c=$panelW,r=$bodyH,z=0$ST")
        return vbox(
            separatorEmpty(),
            header,
            separatorEmpty(),
            vbox(*(0 until bodyH).map { text("") }.toTypedArray()).flex(),
        ).flex()
    }

    private fun renderImageHeader(content: Content, width: Int?, height: Int?): Element {
        val dims = if (width != null && height != null) "  ${width}×${height}px" else ""
        val mode = if (kittySupported) "  kitty" else ""
        return hbox(
            text(" Response Body").bold().color(black),
            if (content.mimeType.isNotEmpty())
                hbox(text("  "), text(content.mimeType).color(black).underlined())
            else text(""),
            text("  ${content.size}B").color(black),
            text(dims).color(black),
            if (mode.isNotEmpty()) text(mode).color(Color.GrayDark).dim() else text(""),
            filler(),
        ).bgcolor(beige)
    }

    private fun renderHalfBlock(image: DecodedImage, content: Content, panelW: Int, bodyH: Int): Element {
        val dstW = minOf(image.width, panelW)
        val scale = image.width.toDouble() / dstW
        val dstH = (image.height / scale).toInt()
        val termRows = (dstH + 1) / 2
        val visibleRows = maxOf(0, minOf(termRows - scrollY.value, bodyH))
        val imageElements = (0 until visibleRows).map { t ->
            val absRow = scrollY.value + t
            val dstY0 = absRow * 2
            val dstY1 = dstY0 + 1
            hbox(*(0 until dstW).map { dstX ->
                val srcX = (dstX * scale).toInt().coerceIn(0, image.width - 1)
                val srcY0 = (dstY0 * scale).toInt().coerceIn(0, image.height - 1)
                val srcY1 = (dstY1 * scale).toInt().coerceIn(0, image.height - 1)
                text("▀").color(pixelColor(image, srcX, srcY0)).bgcolor(pixelColor(image, srcX, srcY1))
            }.toTypedArray())
        }
        return vbox(
            separatorEmpty(),
            renderImageHeader(content, image.width, image.height),
            separatorEmpty(),
            hbox(
                vbox(*imageElements.toTypedArray()).flex(),
                vScrollBar(scrollY.value, termRows, bodyH),
            ).flex(),
        ).flex()
    }

    private fun pixelColor(image: DecodedImage, x: Int, y: Int): Color {
        val idx = (y * image.width + x) * 4
        val r = (image.pixels[idx].toInt() and 0xFF).toUByte()
        val g = (image.pixels[idx + 1].toInt() and 0xFF).toUByte()
        val b = (image.pixels[idx + 2].toInt() and 0xFF).toUByte()
        return Color.rgb(r, g, b)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun uploadKittyImage(bytes: ByteArray, id: Int) {
        val chunks = Base64.encode(bytes).chunked(4096)
        chunks.forEachIndexed { i, chunk ->
            val more = if (i == chunks.lastIndex) 0 else 1
            val apc = if (i == 0) "${ESC}_Ga=t,f=100,q=2,i=$id,m=$more;$chunk$ST"
                      else "${ESC}_Gm=$more;$chunk$ST"
            writeToStdout(apc)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getImageBytes(content: Content): ByteArray? {
        val text = content.text ?: return null
        return when {
            content.encoding == "base64" -> try { Base64.decode(text) } catch (_: Exception) { null }
            text.startsWith("data:") -> {
                val comma = text.indexOf(',')
                if (comma < 0) return null
                val header = text.substring(0, comma)
                val data = text.substring(comma + 1)
                if (header.contains("base64")) try { Base64.decode(data) } catch (_: Exception) { null }
                else data.encodeToByteArray()
            }
            else -> text.encodeToByteArray()
        }
    }

    private fun decodeImageBytes(bytes: ByteArray): DecodedImage? {
        if (bytes.isEmpty()) return null
        return memScoped {
            val width = alloc<IntVar>()
            val height = alloc<IntVar>()
            val channels = alloc<IntVar>()
            val pixels = bytes.usePinned { pinned ->
                stbi_load_from_memory(pinned.addressOf(0).reinterpret(), bytes.size, width.ptr, height.ptr, channels.ptr, 4)
            } ?: return@memScoped null
            val w = width.value
            val h = height.value
            val pixelData = pixels.reinterpret<ByteVar>().readBytes(w * h * 4)
            stbi_image_free(pixels)
            DecodedImage(pixelData, w, h)
        }
    }
}

internal fun isImageContent(content: har.Content, requestUrl: String = ""): Boolean {
    val mime = content.mimeType.substringBefore(';').trim().lowercase()
    return mime.startsWith("image/")
        || content.text?.startsWith("data:image/") == true
        || requestUrl.startsWith("data:image/")
}

@OptIn(ExperimentalForeignApi::class)
private fun isKittySupported(): Boolean {
    val kittyId  = getenv("KITTY_WINDOW_ID")?.toKString()
    val term     = getenv("TERM")?.toKString()
    val termProg = getenv("TERM_PROGRAM")?.toKString()
    val wezterm  = getenv("WEZTERM_EXECUTABLE")?.toKString()
    val konsole  = getenv("KONSOLE_VERSION")?.toKString()
    return kittyId?.isNotEmpty() == true
        || term == "xterm-kitty"
        || term == "xterm-ghostty"
        || term == "foot" || term == "foot-extra"
        || termProg?.equals("kitty",   ignoreCase = true) == true
        || termProg?.equals("ghostty", ignoreCase = true) == true
        || termProg?.equals("WezTerm", ignoreCase = true) == true
        || wezterm?.isNotEmpty() == true
        || konsole?.isNotEmpty() == true
}

@OptIn(ExperimentalForeignApi::class)
private fun writeToStdout(s: String) {
    val bytes = s.encodeToByteArray()
    bytes.usePinned { pinned ->
        write(1, pinned.addressOf(0), bytes.size.convert())
    }
}
