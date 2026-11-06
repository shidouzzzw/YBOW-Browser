package com.ybow.browser.browser

import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID

/**
 * One open tab. Every tab owns its own [WebView] instance so history, scroll
 * position and form state survive tab switching.
 */
class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val isIncognito: Boolean = false,
) {
    var webView: WebView? = null

    var title by mutableStateOf("")
    var url by mutableStateOf("")
    var favicon by mutableStateOf<ImageBitmap?>(null)
    var progress by mutableIntStateOf(0)
    var isLoading by mutableStateOf(false)
    var canGoBack by mutableStateOf(false)
    var canGoForward by mutableStateOf(false)
    var isSecure by mutableStateOf(false)

    val hasPage: Boolean get() = url.isNotBlank() && url != "about:blank"
}
