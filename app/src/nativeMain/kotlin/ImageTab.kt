import har.Content
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import nl.ncaj.*
import platform.posix.*
import stb_image.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val ESC = ""
private const val ST = "\\"   // APC string terminator (ESC \)

@OptIn(ExperimentalForeignApi::class)
internal class ImageTab(
    private val appState: AppState,
    private val bgScope: CoroutineScope,
    private val requestFrame: () -> Unit,
) {
    private val scrollY = IntState(0)
    private var lastEntryIdx = -1
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
        if (kittySupported) return false
        val image = decodedImage ?: return false
        val panelW = maxOf(1, Terminal.size().dimx - appState.leftSize.value - 2 - 1)
        val dstW = minOf(image.width, panelW)
        val scaleY = image.width.toDouble() / dstW
        val dstH = (image.height / scaleY).toInt()
        val termRows = (dstH + 1) / 2
        return handleScrollEvents(event, prevKey, scrollY, termRows, contentHeight)
    }

    fun deactivate() {
        if (kittySupported && kittyUploaded) {
            writeToStdout("${ESC}_Ga=d,d=i,q=2,i=$kittyImageId$ST")
            kittyUploaded = false
        }
    }

    fun free() {
        deactivate()
        scrollY.free()
    }

    private fun render(): Element {
        val currentIdx = appState.selectedEntry.value
        val entry = appState.entries[currentIdx]
        val content = entry.response.content

        if (currentIdx != lastEntryIdx) {
            deactivate()
            lastEntryIdx = currentIdx
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
        val bodyH = maxOf(1, Terminal.size().dimy - 8)

        return if (kittySupported) {
            renderKitty(imageBytes, content, panelW, bodyH)
        } else {
            val image = decodedImage
            when {
                image != null -> renderHalfBlock(image, content, panelW, bodyH)
                decodeJob != null -> vbox(renderHeader(content, null, null), separatorEmpty(), text("  Decoding image…").dim().flex()).flex()
                else -> vbox(renderHeader(content, null, null), separatorEmpty(), text("  Unable to decode image").dim().flex()).flex()
            }
        }
    }

    private fun renderKitty(bytes: ByteArray?, content: Content, panelW: Int, bodyH: Int): Element {
        val header = renderHeader(content, null, null)
        if (bytes == null) {
            return vbox(header, separatorEmpty(), text("  Unable to decode image").dim().flex()).flex()
        }
        if (!kittyUploaded) {
            kittyImageId = (kittyImageId % 9999) + 1
            uploadKittyImage(bytes, kittyImageId)
            kittyUploaded = true
        }
        // Row layout (1-indexed ANSI): 1=border 2=tabbar 3=sep 4=imgheader 5=sep-empty 6=image
        val imageRow = 6
        val imageCol = appState.leftSize.value + 2
        writeToStdout("${ESC}[${imageRow};${imageCol}H${ESC}_Ga=p,q=2,i=$kittyImageId,c=$panelW,r=$bodyH,z=0$ST")
        return vbox(
            header,
            separatorEmpty(),
            vbox(*(0 until bodyH).map { text("") }.toTypedArray()).flex(),
        ).flex()
    }

    private fun renderHeader(content: Content, width: Int?, height: Int?): Element {
        val dims = if (width != null && height != null) "  ${width}×${height}px" else ""
        val mode = if (kittySupported) "  kitty" else ""
        return hbox(
            text(" Image").bold().color(black),
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
            renderHeader(content, image.width, image.height),
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

@OptIn(ExperimentalForeignApi::class)
private fun isKittySupported(): Boolean {
    val kittyId   = getenv("KITTY_WINDOW_ID")?.toKString()
    val term      = getenv("TERM")?.toKString()
    val termProg  = getenv("TERM_PROGRAM")?.toKString()
    val wezterm   = getenv("WEZTERM_EXECUTABLE")?.toKString()
    val konsole   = getenv("KONSOLE_VERSION")?.toKString()
    return kittyId?.isNotEmpty() == true                           // kitty
        || term == "xterm-kitty"                                   // kitty (alternate)
        || term == "xterm-ghostty"                                 // ghostty
        || term == "foot" || term == "foot-extra"                  // foot
        || termProg?.equals("kitty",   ignoreCase = true) == true  // kitty
        || termProg?.equals("ghostty", ignoreCase = true) == true  // ghostty
        || termProg?.equals("WezTerm", ignoreCase = true) == true  // wezterm
        || wezterm?.isNotEmpty() == true                           // wezterm (fallback)
        || konsole?.isNotEmpty() == true                           // konsole
}

@OptIn(ExperimentalForeignApi::class)
private fun writeToStdout(s: String) {
    val bytes = s.encodeToByteArray()
    bytes.usePinned { pinned ->
        write(1, pinned.addressOf(0), bytes.size.convert())
    }
}

internal fun isImageContent(content: har.Content, requestUrl: String = ""): Boolean {
    val mime = content.mimeType.substringBefore(';').trim().lowercase()
    return mime.startsWith("image/")
        || content.text?.startsWith("data:image/") == true
        || requestUrl.startsWith("data:image/")
}
