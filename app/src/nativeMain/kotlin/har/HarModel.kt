package har

import kotlinx.serialization.Serializable

@Serializable
data class HarFile(val log: HarLog)

@Serializable
data class HarLog(val entries: List<HarEntry>)

@Serializable
data class HarEntry(
    val request: HarRequest,
    val response: HarResponse,
    val timings: HarTimings,
    val time: Double = 0.0,
)

@Serializable
data class HarRequest(
    val method: String,
    val url: String,
    val headers: List<HarHeader> = emptyList(),
    val postData: HarPostData? = null,
)

@Serializable
data class HarResponse(
    val status: Int,
    val statusText: String = "",
    val headers: List<HarHeader> = emptyList(),
    val content: HarContent = HarContent(),
)

@Serializable
data class HarHeader(
    val name: String,
    val value: String,
)

@Serializable
data class HarContent(
    val mimeType: String = "",
    val text: String? = null,
)

@Serializable
data class HarTimings(
    val blocked: Double = -1.0,
    val dns: Double = -1.0,
    val connect: Double = -1.0,
    val send: Double = 0.0,
    val wait: Double = 0.0,
    val receive: Double = 0.0,
    val ssl: Double = -1.0,
)

@Serializable
data class HarPostData(
    val mimeType: String = "",
    val text: String? = null,
)
