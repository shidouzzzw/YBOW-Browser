package com.ybow.browser.browser

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Navigation client for a single tab.
 *
 * SSL errors are never ignored: the connection is cancelled so a user cannot be
 * silently downgraded into a man-in-the-middle session.
 */
class YbowWebViewClient(
    private val host: TabHost,
    private val tab: BrowserTab,
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
        host.tabShouldOverrideUrl(view.context, request.url.toString())

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        host.tabPageStarted(tab, url)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        host.tabPageFinished(tab, url)
    }

    override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        host.tabNavigationStateChanged(tab)
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.cancel()
        host.tabSslError(tab, error.url.orEmpty())
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError,
    ) {
        super.onReceivedError(view, request, error)
        if (request.isForMainFrame) {
            host.tabPageError(tab, error.description?.toString().orEmpty())
        }
    }
}
