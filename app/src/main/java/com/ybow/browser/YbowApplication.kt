package com.ybow.browser

import android.app.Application
import android.webkit.WebView
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.ybow.browser.data.BrowserRepository
import com.ybow.browser.data.DownloadRepository

class YbowApplication : Application() {

    val repository: BrowserRepository by lazy { BrowserRepository(this) }
    val downloadRepository: DownloadRepository by lazy { DownloadRepository(this) }

    override fun onCreate() {
        super.onCreate()

        // Enable the Chromium Safe Browsing database when the WebView build supports it.
        if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
            WebViewCompat.startSafeBrowsing(this) { /* No data leaves the device. */ }
        }

        // Remote debugging stays off in release builds.
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }
}
