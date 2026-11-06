package com.ybow.browser.browser

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

/**
 * Chrome-level callbacks: progress, title, favicon, file uploads and popup windows.
 */
class YbowWebChromeClient(
    private val host: TabHost,
    private val tab: BrowserTab,
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        host.tabProgress(tab, newProgress)
    }

    override fun onReceivedTitle(view: WebView, title: String?) {
        host.tabTitle(tab, title)
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        host.tabFavicon(tab, icon)
    }

    override fun onShowFileChooser(
        view: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams,
    ): Boolean {
        host.tabFileChooser(tab, filePathCallback, fileChooserParams)
        return true
    }

    override fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?,
    ): Boolean = host.tabCreateWindow(tab, resultMsg)

    override fun onPermissionRequest(request: PermissionRequest) {
        // YBOW never silently hands camera, microphone or geolocation access to a page.
        request.deny()
    }
}
