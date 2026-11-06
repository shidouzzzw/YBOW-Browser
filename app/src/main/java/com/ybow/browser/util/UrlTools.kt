package com.ybow.browser.util

import com.ybow.browser.data.SearchEngine

/**
 * Pure Kotlin URL handling so the behaviour of the address bar can be unit tested
 * on the JVM without an Android runtime.
 */
object UrlTools {

    private val hostPattern = Regex(
        "^([a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}(:[0-9]{1,5})?(/.*)?$"
    )
    private val localhostPattern = Regex(
        "^(localhost|127\\.0\\.0\\.1|\\[::1\\])(:[0-9]{1,5})?(/.*)?$"
    )

    /** Returns true when the text looks like a host name that should be loaded as HTTPS. */
    fun looksLikeHost(input: String): Boolean =
        hostPattern.matches(input) || localhostPattern.matches(input)

    fun isHttpUrl(url: String): Boolean =
        url.startsWith("http://", ignoreCase = true) || url.startsWith("https://", ignoreCase = true)

    fun isSecure(url: String): Boolean = url.startsWith("https://", ignoreCase = true)

    /**
     * Turns whatever the user typed into either a navigable URL or a search query URL.
     * An empty input returns an empty string.
     */
    fun toUrlOrSearch(rawInput: String, engine: SearchEngine): String {
        val input = rawInput.trim()
        if (input.isEmpty()) return ""

        val lower = input.lowercase()
        if (lower.startsWith("http://") ||
            lower.startsWith("https://") ||
            lower.startsWith("about:") ||
            lower.startsWith("file:")
        ) {
            return input
        }

        if (!input.contains(' ') && looksLikeHost(input)) {
            return "https://$input"
        }

        return engine.search(input)
    }

    /** Short, human friendly form of a URL for the address bar and list rows. */
    fun displayUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        var value = url
        value = value.removePrefix("https://").removePrefix("http://")
        value = value.removePrefix("www.")
        if (value.endsWith("/") && value.length > 1) value = value.dropLast(1)
        return value
    }

    /** Host name of a URL, or an empty string when the URL has no host. */
    fun hostOf(url: String?): String {
        if (url.isNullOrBlank()) return ""
        var value = url
        val schemeIndex = value.indexOf("://")
        if (schemeIndex >= 0) value = value.substring(schemeIndex + 3)
        val end = value.indexOfFirst { it == '/' || it == '?' || it == '#' }
        if (end >= 0) value = value.substring(0, end)
        value = value.removePrefix("www.")
        return value
    }
}
