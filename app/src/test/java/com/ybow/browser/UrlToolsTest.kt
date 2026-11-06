package com.ybow.browser

import com.ybow.browser.data.SearchEngine
import com.ybow.browser.util.UrlTools
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlToolsTest {

    @Test
    fun fullUrlIsKept() {
        assertEquals(
            "https://example.com/path?q=1",
            UrlTools.toUrlOrSearch("https://example.com/path?q=1", SearchEngine.GOOGLE),
        )
    }

    @Test
    fun bareHostBecomesHttpsUrl() {
        assertEquals(
            "https://example.com",
            UrlTools.toUrlOrSearch("example.com", SearchEngine.GOOGLE),
        )
    }

    @Test
    fun hostWithPathBecomesHttpsUrl() {
        assertEquals(
            "https://developer.android.com/guide",
            UrlTools.toUrlOrSearch("developer.android.com/guide", SearchEngine.GOOGLE),
        )
    }

    @Test
    fun wordsBecomeASearchQuery() {
        assertEquals(
            "https://duckduckgo.com/?q=android+compose",
            UrlTools.toUrlOrSearch("android compose", SearchEngine.DUCKDUCKGO),
        )
    }

    @Test
    fun singleWordBecomesGoogleQuery() {
        assertEquals(
            "https://www.google.com/search?q=weather",
            UrlTools.toUrlOrSearch("weather", SearchEngine.GOOGLE),
        )
    }

    @Test
    fun emptyInputStaysEmpty() {
        assertEquals("", UrlTools.toUrlOrSearch("   ", SearchEngine.GOOGLE))
    }

    @Test
    fun localhostIsTreatedAsHost() {
        assertEquals(
            "https://localhost:8080",
            UrlTools.toUrlOrSearch("localhost:8080", SearchEngine.GOOGLE),
        )
    }

    @Test
    fun displayUrlDropsSchemeAndWww() {
        assertEquals("example.com/page", UrlTools.displayUrl("https://www.example.com/page"))
        assertEquals("example.com", UrlTools.displayUrl("http://example.com/"))
    }

    @Test
    fun hostOfExtractsAuthority() {
        assertEquals("example.com", UrlTools.hostOf("https://www.example.com/a/b"))
        assertEquals("", UrlTools.hostOf(null))
    }

    @Test
    fun schemeChecksAreCaseInsensitive() {
        assertTrue(UrlTools.isHttpUrl("HTTPS://example.com"))
        assertTrue(UrlTools.isSecure("https://example.com"))
        assertFalse(UrlTools.isSecure("http://example.com"))
        assertFalse(UrlTools.isHttpUrl("about:blank"))
    }
}
