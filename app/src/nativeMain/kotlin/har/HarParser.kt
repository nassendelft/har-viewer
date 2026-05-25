@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package har

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.serialization.json.Json
import platform.posix.*

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

private fun readFileContent(path: String): String {
    val file = fopen(path, "r") ?: run {
        val reason = strerror(errno)?.toKString() ?: "unknown error"
        error("Cannot open file '$path': $reason")
    }
    try {
        fseek(file, 0, SEEK_END)
        val size = ftell(file).toInt()
        if (size < 0) error("Cannot determine size of file '$path'")
        rewind(file)
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            val read = fread(pinned.addressOf(0), 1.toULong(), size.toULong(), file).toInt()
            if (read != size) error("Could not read file '$path': read $read of $size bytes")
        }
        return bytes.decodeToString()
    } finally {
        fclose(file)
    }
}

fun parseHar(path: String): HarFile {
    val content = readFileContent(path)
    return try {
        json.decodeFromString(content)
    } catch (e: Exception) {
        error("Failed to parse '$path' as a HAR file: ${e.message}")
    }
}
