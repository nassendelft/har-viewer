package har

import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────
// Root
// ─────────────────────────────────────────────

/**
 * The root wrapper of a HAR file.
 * The top-level JSON object MUST contain a single key "log".
 */
@Serializable
data class HarFile(
    val log: Log,
)

// ─────────────────────────────────────────────
// Log
// ─────────────────────────────────────────────

/**
 * Represents the root of the exported data (HAR 1.2).
 *
 * @property version  Version number of the HAR format (e.g. "1.2").
 * @property creator  Information about the application that created the log.
 * @property browser  Information about the browser that created the log.
 * @property pages    List of exported pages. May be omitted if the tool does
 *                    not support grouping by page.
 * @property entries  List of all exported HTTP requests, one per request.
 * @property comment  Optional free-form annotation.
 */
@Serializable
data class Log(
    val version: String,
    val creator: Creator,
    val browser: Browser? = null,
    val pages: List<Page>? = null,
    val entries: List<Entry>,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Creator / Browser
// ─────────────────────────────────────────────

/**
 * Information about the application that created the HAR log.
 *
 * @property name     Application name.
 * @property version  Application version.
 * @property comment  Optional annotation.
 */
@Serializable
data class Creator(
    val name: String,
    val version: String,
    val comment: String? = null,
)

/**
 * Information about the browser that created the HAR log.
 *
 * @property name     Browser name.
 * @property version  Browser version.
 * @property comment  Optional annotation.
 */
@Serializable
data class Browser(
    val name: String,
    val version: String,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Page
// ─────────────────────────────────────────────

/**
 * Represents one exported web page.
 *
 * @property startedDateTime  ISO 8601 timestamp of when the page load began
 *                            (e.g. "2009-07-24T19:20:30.45+01:00").
 * @property id               Unique identifier of this page within the log.
 *                            Entries reference their parent page via this id.
 * @property title            Page title.
 * @property pageTimings      Detailed timing information for the page load.
 * @property comment          Optional annotation.
 */
@Serializable
data class Page(
    val startedDateTime: String,
    val id: String,
    val title: String,
    val pageTimings: PageTimings,
    val comment: String? = null,
)

/**
 * Timing information for a page load.
 * All values are in milliseconds since [Page.startedDateTime].
 * Use -1 when a timing does not apply to the current request.
 *
 * @property onContentLoad  Time until the page content finished loading.
 * @property onLoad         Time until the onLoad event fired.
 * @property comment        Optional annotation.
 */
@Serializable
data class PageTimings(
    val onContentLoad: Double? = null,
    val onLoad: Double? = null,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Entry
// ─────────────────────────────────────────────

/**
 * Represents one exported (tracked) HTTP request/response pair.
 *
 * @property pageref           Reference to the parent [Page.id].
 *                             Omit when the tool does not support page grouping.
 * @property startedDateTime   ISO 8601 timestamp of when the request started.
 * @property time              Total elapsed time of the request in milliseconds.
 *                             Equals the sum of all non-(-1) values in [timings].
 * @property request           Detailed request information.
 * @property response          Detailed response information.
 * @property cache             Cache usage information.
 * @property timings           Detailed round-trip timing breakdown.
 * @property serverIPAddress   IP address of the connected server (post-DNS).
 * @property connection        Unique identifier for the parent TCP/IP connection
 *                             (e.g. client port number).
 * @property comment           Optional annotation.
 */
@Serializable
data class Entry(
    val pageref: String? = null,
    val startedDateTime: String,
    val time: Double,
    val request: Request,
    val response: Response,
    val cache: Cache,
    val timings: Timings,
    val serverIPAddress: String? = null,
    val connection: String? = null,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Request
// ─────────────────────────────────────────────

/**
 * Detailed information about an HTTP request.
 *
 * @property method       HTTP method (GET, POST, PUT, …).
 * @property url          Absolute URL (fragments excluded).
 * @property httpVersion  HTTP version (e.g. "HTTP/1.1").
 * @property cookies      List of cookies sent with the request.
 * @property headers      List of request headers.
 * @property queryString  Parsed query-string parameters.
 * @property postData     Posted body data (present for POST/PUT, etc.).
 * @property headersSize  Byte length from start of message to end of double
 *                        CRLF before the body. -1 if unavailable.
 * @property bodySize     Byte length of the request body. -1 if unavailable.
 * @property comment      Optional annotation.
 */
@Serializable
data class Request(
    val method: String,
    val url: String,
    val httpVersion: String,
    val cookies: List<Cookie>,
    val headers: List<Header>,
    val queryString: List<QueryStringParam>,
    val postData: PostData? = null,
    val headersSize: Int,
    val bodySize: Int,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Response
// ─────────────────────────────────────────────

/**
 * Detailed information about an HTTP response.
 *
 * @property status       HTTP status code (e.g. 200).
 * @property statusText   HTTP status description (e.g. "OK").
 * @property httpVersion  HTTP version (e.g. "HTTP/1.1").
 * @property cookies      List of cookies received with the response.
 * @property headers      List of response headers.
 * @property content      Details about the response body.
 * @property redirectURL  Target URL from the Location header (empty string
 *                        if not a redirect).
 * @property headersSize  Byte length from start of message to end of double
 *                        CRLF before the body. -1 if unavailable.
 * @property bodySize     Byte length of the received body. 0 for 304 responses
 *                        served from cache. -1 if unavailable.
 * @property comment      Optional annotation.
 */
@Serializable
data class Response(
    val status: Int,
    val statusText: String,
    val httpVersion: String,
    val cookies: List<Cookie>,
    val headers: List<Header>,
    val content: Content,
    val redirectURL: String,
    val headersSize: Int,
    val bodySize: Int,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Cookie
// ─────────────────────────────────────────────

/**
 * Represents an HTTP cookie (used in both requests and responses).
 *
 * @property name      Cookie name.
 * @property value     Cookie value.
 * @property path      Cookie path scope.
 * @property domain    Cookie domain scope.
 * @property expires   Expiration timestamp (ISO 8601).
 * @property httpOnly  True when the cookie is HTTP-only.
 * @property secure    True when the cookie was transmitted over SSL/TLS.
 * @property comment   Optional annotation.
 */
@Serializable
data class Cookie(
    val name: String,
    val value: String,
    val path: String? = null,
    val domain: String? = null,
    val expires: String? = null,
    val httpOnly: Boolean? = null,
    val secure: Boolean? = null,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────

/**
 * A single HTTP header name/value pair.
 *
 * @property name     Header field name.
 * @property value    Header field value.
 * @property comment  Optional annotation.
 */
@Serializable
data class Header(
    val name: String,
    val value: String,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// QueryStringParam
// ─────────────────────────────────────────────

/**
 * A single parsed query-string parameter.
 *
 * @property name     Parameter name.
 * @property value    Parameter value.
 * @property comment  Optional annotation.
 */
@Serializable
data class QueryStringParam(
    val name: String,
    val value: String,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// PostData
// ─────────────────────────────────────────────

/**
 * Information about data posted in the request body.
 *
 * Either [params] or [text] must be present; both MAY be present.
 *
 * @property mimeType  MIME type of the posted data.
 * @property params    List of posted form parameters (URL-encoded body).
 * @property text      Raw posted data as plain text.
 * @property comment   Optional annotation.
 */
@Serializable
data class PostData(
    val mimeType: String,
    val params: List<Param> = emptyList(),
    val text: String? = null,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Param
// ─────────────────────────────────────────────

/**
 * A single posted form parameter (or file upload part).
 *
 * @property name         Parameter name.
 * @property value        Parameter value or file content.
 * @property fileName     Name of the uploaded file (if applicable).
 * @property contentType  Content type of the uploaded file (if applicable).
 * @property comment      Optional annotation.
 */
@Serializable
data class Param(
    val name: String,
    val value: String? = null,
    val fileName: String? = null,
    val contentType: String? = null,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────

/**
 * Details about the response body content.
 *
 * @property size         Length of the returned content in bytes.
 *                        Should equal [Response.bodySize] when uncompressed,
 *                        or be larger when the body was compressed.
 * @property compression  Number of bytes saved by compression.
 *                        Omit if not available.
 * @property mimeType     MIME type of the response text, including charset
 *                        attribute when present (value of Content-Type header).
 * @property text         Response body as text, either HTTP-decoded or encoded
 *                        (see [encoding]). Omit if not available.
 * @property encoding     Encoding applied to [text] (e.g. "base64").
 *                        Omit when [text] is plain UTF-8.
 * @property comment      Optional annotation.
 */
@Serializable
data class Content(
    val size: Long,
    val compression: Long? = null,
    val mimeType: String,
    val text: String? = null,
    val encoding: String? = null,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Cache
// ─────────────────────────────────────────────

/**
 * Cache usage information for an [Entry].
 *
 * @property beforeRequest  Cache state before the request was sent.
 * @property afterRequest   Cache state after the response was received.
 * @property comment        Optional annotation.
 */
@Serializable
data class Cache(
    val beforeRequest: CacheState? = null,
    val afterRequest: CacheState? = null,
    val comment: String? = null,
)

/**
 * State of a cache entry at a point in time.
 *
 * @property expires     Expiration time of the cache entry (ISO 8601).
 * @property lastAccess  Timestamp when the entry was last opened (ISO 8601).
 * @property eTag        ETag of the cached resource.
 * @property hitCount    Number of times this cache entry has been opened.
 * @property comment     Optional annotation.
 */
@Serializable
data class CacheState(
    val expires: String? = null,
    val lastAccess: String,
    val eTag: String,
    val hitCount: Int,
    val comment: String? = null,
)

// ─────────────────────────────────────────────
// Timings
// ─────────────────────────────────────────────

/**
 * Detailed timing breakdown for a single request/response round-trip.
 *
 * All values are in milliseconds. Use -1 when a phase does not apply.
 * The [Entry.time] total equals the sum of all non-(-1) values here.
 *
 * @property blocked  Time queued waiting for a free network connection.
 * @property dns      DNS resolution time.
 * @property connect  Time to establish the TCP connection.
 * @property send     Time to transmit the HTTP request to the server.
 * @property wait     Time waiting for the first byte of the response.
 * @property receive  Time to read the complete response from server or cache.
 * @property ssl      Time for SSL/TLS negotiation (also included in [connect]
 *                    for backward compatibility with HAR 1.1).
 * @property comment  Optional annotation.
 */
@Serializable
data class Timings(
    val blocked: Double? = null,
    val dns: Double? = null,
    val connect: Double? = null,
    val send: Double,
    val wait: Double,
    val receive: Double,
    val ssl: Double? = null,
    val comment: String? = null,
)