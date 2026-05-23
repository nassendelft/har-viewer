@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package har

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.serialization.json.Json
import platform.posix.*

private val json = Json { ignoreUnknownKeys = true }

private fun readFileContent(path: String): String {
    val file = fopen(path, "r") ?: error("Cannot open file: $path")
    try {
        fseek(file, 0, SEEK_END)
        val size = ftell(file).toInt()
        rewind(file)
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            fread(pinned.addressOf(0), 1.toULong(), size.toULong(), file)
        }
        return bytes.decodeToString()
    } finally {
        fclose(file)
    }
}

fun parseHar(path: String): HarFile = json.decodeFromString(readFileContent(path))
