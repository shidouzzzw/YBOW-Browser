package com.ybow.browser.util

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * Loads and caches a site's own favicon for the home screen shortcuts. The only
 * request made is to the shortcut's own site, and results are kept in memory only.
 */
object FaviconStore {

    private val cache = ConcurrentHashMap<String, ImageBitmap?>()
    private val attempted = ConcurrentHashMap.newKeySet<String>()

    fun cached(url: String): ImageBitmap? = cache[UrlTools.hostOf(url)]

    suspend fun load(url: String): ImageBitmap? = withContext(Dispatchers.IO) {
        val host = UrlTools.hostOf(url)
        if (host.isBlank()) return@withContext null
        cache[host]?.let { return@withContext it }
        if (!attempted.add(host)) return@withContext cache[host]

        val bitmap = fetch("https://$host/favicon.ico")
            ?: fetch("https://$host/apple-touch-icon.png")
        val image = bitmap?.asImageBitmap()
        cache[host] = image
        image
    }

    private fun fetch(address: String): android.graphics.Bitmap? = try {
        val connection = (URL(address).openConnection() as HttpURLConnection).apply {
            connectTimeout = 4000
            readTimeout = 4000
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0 (Android) YBOW")
        }
        try {
            if (connection.responseCode in 200..299) {
                connection.inputStream.use { BitmapFactory.decodeStream(it) }
            } else {
                null
            }
        } finally {
            connection.disconnect()
        }
    } catch (error: Exception) {
        null
    }
}
