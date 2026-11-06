package com.ybow.browser.browser

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

/**
 * Bridge between a tab's WebView clients and the ViewModel.
 */
interface TabHost {
    fun tabPageStarted(tab: BrowserTab, url: String)
    fun tabPageFinished(tab: BrowserTab, url: String)
    fun tabProgress(tab: BrowserTab, progress: Int)
    fun tabTitle(tab: BrowserTab, title: String?)
    fun tabFavicon(tab: BrowserTab, icon: Bitmap?)
    fun tabNavigationStateChanged(tab: BrowserTab)
    fun tabSslError(tab: BrowserTab, url: String)
    fun tabPageError(tab: BrowserTab, description: String)
    fun tabFileChooser(
        tab: BrowserTab,
        callback: ValueCallback<Array<Uri>>,
        params: WebChromeClient.FileChooserParams,
    )
    fun tabCreateWindow(tab: BrowserTab, resultMsg: Message?): Boolean
    fun tabShouldOverrideUrl(context: Context, url: String): Boolean
}
