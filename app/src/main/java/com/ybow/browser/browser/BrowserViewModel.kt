package com.ybow.browser.browser

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ybow.browser.R
import com.ybow.browser.YbowApplication
import com.ybow.browser.data.AppSettings
import com.ybow.browser.data.DownloadStatus
import com.ybow.browser.data.SearchEngine
import com.ybow.browser.data.SettingsRepository
import com.ybow.browser.data.ThemeMode
import com.ybow.browser.data.local.Bookmark
import com.ybow.browser.data.local.DownloadRecord
import com.ybow.browser.data.local.HistoryEntry
import com.ybow.browser.util.UrlTools
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Owns every live tab and its WebView. All browser state is local to the device.
 */
class BrowserViewModel(application: Application) : AndroidViewModel(application), TabHost {

    private val app = getApplication<YbowApplication>()
    private val browserRepository = app.repository
    private val downloadRepository = app.downloadRepository
    private val settingsRepository = SettingsRepository(application)

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    private var currentSettings = AppSettings()

    val tabs = mutableStateListOf<BrowserTab>()

    var activeTabId by mutableStateOf<String?>(null)
        private set

    val activeTab: BrowserTab?
        get() = tabs.firstOrNull { it.id == activeTabId }

    val bookmarks: Flow<List<Bookmark>> = browserRepository.bookmarks
    val history: Flow<List<HistoryEntry>> = browserRepository.history
    val recentHistory: Flow<List<HistoryEntry>> = browserRepository.recentHistory
    val downloads: Flow<List<DownloadRecord>> = downloadRepository.records

    var snackbar by mutableStateOf<String?>(null)
        private set

    var pendingFileChooser by mutableStateOf<WebChromeClient.FileChooserParams?>(null)
        private set

    private var host: FrameLayout? = null
    private var pendingUrl: String? = null
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { updated ->
                currentSettings = updated
                tabs.forEach { tab -> tab.webView?.let { applyLiveSettings(it) } }
            }
        }
    }

    // ---------------------------------------------------------------- tabs

    /** Attaches the WebView container, creating the first tab on first attach. */
    fun attachHost(container: FrameLayout) {
        if (host !== container) {
            host = container
            container.removeAllViews()
        }
        if (tabs.isEmpty()) {
            val initial = pendingUrl
            pendingUrl = null
            openNewTab(url = initial)
        } else {
            attachActiveWebView()
        }
    }

    fun openNewTab(url: String? = null, incognito: Boolean = false) {
        val tab = createTab(incognito)
        val target = url ?: currentSettings.homepage.trim().takeIf { it.isNotBlank() }
        if (!target.isNullOrBlank()) loadInto(tab, target)
    }

    private fun createTab(incognito: Boolean): BrowserTab {
        val tab = BrowserTab(isIncognito = incognito)
        tab.webView = createWebView(tab)
        tabs.add(tab)
        activeTabId = tab.id
        attachActiveWebView()
        return tab
    }

    fun selectTab(tabId: String) {
        if (tabs.none { it.id == tabId }) return
        if (activeTabId == tabId) {
            attachActiveWebView()
            return
        }
        activeTabId = tabId
        attachActiveWebView()
    }

    fun closeTab(tabId: String) {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index < 0) return
        val closing = tabs[index]
        val wasActive = activeTabId == closing.id
        tabs.removeAt(index)
        destroyTab(closing)

        if (tabs.isEmpty()) {
            activeTabId = null
            openNewTab()
        } else if (wasActive) {
            val next = tabs[index.coerceAtMost(tabs.lastIndex)]
            activeTabId = next.id
            attachActiveWebView()
        }
    }

    private fun attachActiveWebView() {
        val container = host ?: return
        val webView = activeTab?.webView
        if (webView != null && webView.parent === container && container.childCount == 1) return

        container.removeAllViews()
        if (webView != null) {
            (webView.parent as? ViewGroup)?.removeView(webView)
            container.addView(
                webView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                ),
            )
            webView.onResume()
        }
    }

    private fun destroyTab(tab: BrowserTab) {
        val webView = tab.webView ?: return
        tab.webView = null
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.stopLoading()
        webView.webViewClient = WebViewClientNoop
        webView.webChromeClient = null
        webView.setDownloadListener(null)
        webView.loadUrl("about:blank")
        webView.removeAllViews()
        webView.destroy()
    }

    // ------------------------------------------------------------ navigation

    fun navigate(input: String) {
        val tab = activeTab ?: return
        loadInto(tab, input)
    }

    private fun loadInto(tab: BrowserTab, input: String) {
        val url = UrlTools.toUrlOrSearch(input, currentSettings.searchEngine)
        if (url.isBlank()) return
        tab.url = url
        tab.isLoading = true
        tab.progress = 0
        tab.webView?.loadUrl(url)
    }

    fun openUrlInNewTab(url: String, incognito: Boolean = false) {
        openNewTab(url = url, incognito = incognito)
    }

    fun goBack() {
        val tab = activeTab ?: return
        if (tab.webView?.canGoBack() == true) tab.webView?.goBack()
    }

    fun goForward() {
        val tab = activeTab ?: return
        if (tab.webView?.canGoForward() == true) tab.webView?.goForward()
    }

    fun reload() {
        activeTab?.webView?.reload()
    }

    fun stopLoading() {
        activeTab?.webView?.stopLoading()
        activeTab?.isLoading = false
    }

    fun goHome() {
        val tab = activeTab ?: return
        val homepage = currentSettings.homepage.trim()
        if (homepage.isNotBlank()) {
            loadInto(tab, homepage)
            return
        }
        tab.webView?.loadUrl("about:blank")
        tab.url = ""
        tab.title = ""
        tab.favicon = null
        tab.progress = 0
        tab.isLoading = false
        tab.canGoBack = false
        tab.canGoForward = false
    }

    fun handleIncomingUrl(url: String?) {
        if (url.isNullOrBlank()) return
        if (host == null) pendingUrl = url else openNewTab(url = url)
    }

    // ------------------------------------------------------------- bookmarks

    fun toggleBookmark(tab: BrowserTab) {
        if (!tab.hasPage) return
        viewModelScope.launch {
            val added = browserRepository.toggleBookmark(tab.url, tab.title)
            showMessage(
                app.getString(
                    if (added) R.string.bookmark_added else R.string.bookmark_removed,
                ),
            )
        }
    }

    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch { browserRepository.removeBookmark(bookmark) }
    }

    fun deleteHistoryEntry(entry: HistoryEntry) {
        viewModelScope.launch { browserRepository.deleteHistoryEntry(entry.id) }
    }

    // ------------------------------------------------------------- downloads

    fun downloadStatus(systemId: Long): DownloadStatus? = downloadRepository.status(systemId)

    fun openDownload(record: DownloadRecord) {
        val intent = downloadRepository.openIntent(record)
        if (intent == null) {
            showMessage(app.getString(R.string.downloads_open_failed))
            return
        }
        try {
            app.startActivity(intent)
        } catch (error: Exception) {
            showMessage(app.getString(R.string.downloads_open_failed))
        }
    }

    fun removeDownload(record: DownloadRecord) {
        viewModelScope.launch { downloadRepository.remove(record) }
    }

    // -------------------------------------------------------------- settings

    fun setSearchEngine(engine: SearchEngine) {
        viewModelScope.launch { settingsRepository.setSearchEngine(engine) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setHomepage(url: String) {
        viewModelScope.launch { settingsRepository.setHomepage(url) }
    }

    fun setJavaScriptEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setJavaScriptEnabled(enabled) }
    }

    fun setThirdPartyCookiesEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setThirdPartyCookiesEnabled(enabled) }
    }

    fun setSaveHistory(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSaveHistory(enabled) }
    }

    fun clearCache() {
        if (tabs.isEmpty()) {
            WebView(app).clearCache(true)
        } else {
            tabs.forEach { it.webView?.clearCache(true) }
        }
        CookieManager.getInstance().flush()
        showMessage(app.getString(R.string.settings_cache_cleared))
    }

    fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
        tabs.forEach { it.webView?.clearFormData() }
        showMessage(app.getString(R.string.settings_cookies_cleared))
    }

    fun clearHistory() {
        viewModelScope.launch {
            browserRepository.clearHistory()
            showMessage(app.getString(R.string.settings_history_cleared))
        }
    }

    fun notifyCopied() {
        showMessage(app.getString(R.string.url_copied))
    }

    fun consumeSnackbar() {
        snackbar = null
    }

    private fun showMessage(text: String) {
        snackbar = text
    }

    // ---------------------------------------------------------------- WebView

    private fun createWebView(tab: BrowserTab): WebView {
        val context = host?.context ?: app
        val webView = WebView(context)
        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        configure(webView, tab)
        webView.webViewClient = YbowWebViewClient(this, tab)
        webView.webChromeClient = YbowWebChromeClient(this, tab)
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            handleDownload(url, userAgent, contentDisposition, mimeType, contentLength)
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(
            webView,
            currentSettings.thirdPartyCookiesEnabled,
        )
        return webView
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configure(webView: WebView, tab: BrowserTab) {
        with(webView.settings) {
            javaScriptEnabled = currentSettings.javaScriptEnabled
            domStorageEnabled = true
            databaseEnabled = true
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = false
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            allowFileAccess = false
            allowContentAccess = true
            safeBrowsingEnabled = true
            setGeolocationEnabled(false)
            userAgentString = "$userAgentString YBOW/1.0"
            if (tab.isIncognito) cacheMode = WebSettings.LOAD_NO_CACHE
        }
    }

    private fun applyLiveSettings(webView: WebView) {
        webView.settings.javaScriptEnabled = currentSettings.javaScriptEnabled
        CookieManager.getInstance().setAcceptThirdPartyCookies(
            webView,
            currentSettings.thirdPartyCookiesEnabled,
        )
    }

    private fun handleDownload(
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long,
    ) {
        viewModelScope.launch {
            val cookie = CookieManager.getInstance().getCookie(url)
            val record = downloadRepository.enqueue(
                url = url,
                userAgent = userAgent,
                cookie = cookie,
                contentDisposition = contentDisposition,
                mimeType = mimeType,
                contentLength = contentLength,
            )
            if (record == null) {
                showMessage(app.getString(R.string.downloads_failed))
            } else {
                showMessage(app.getString(R.string.downloads_started, record.fileName))
            }
        }
    }

    // ------------------------------------------------------------- TabHost

    override fun tabPageStarted(tab: BrowserTab, url: String) {
        tab.url = url
        tab.isLoading = true
        tab.progress = 0
        tab.isSecure = UrlTools.isSecure(url)
        tab.canGoBack = tab.webView?.canGoBack() ?: false
        tab.canGoForward = tab.webView?.canGoForward() ?: false
    }

    override fun tabPageFinished(tab: BrowserTab, url: String) {
        tab.url = url
        tab.isLoading = false
        tab.progress = 100
        tab.isSecure = UrlTools.isSecure(url)
        tab.canGoBack = tab.webView?.canGoBack() ?: false
        tab.canGoForward = tab.webView?.canGoForward() ?: false
        if (tab.title.isBlank()) tab.title = UrlTools.displayUrl(url)
        if (!tab.isIncognito && currentSettings.saveHistory && UrlTools.isHttpUrl(url)) {
            viewModelScope.launch { browserRepository.recordVisit(url, tab.title) }
        }
    }

    override fun tabProgress(tab: BrowserTab, progress: Int) {
        tab.progress = progress
        tab.isLoading = progress < 100
    }

    override fun tabTitle(tab: BrowserTab, title: String?) {
        if (!title.isNullOrBlank()) tab.title = title
    }

    override fun tabFavicon(tab: BrowserTab, icon: Bitmap?) {
        tab.favicon = icon?.asImageBitmap()
    }

    override fun tabNavigationStateChanged(tab: BrowserTab) {
        tab.canGoBack = tab.webView?.canGoBack() ?: false
        tab.canGoForward = tab.webView?.canGoForward() ?: false
    }

    override fun tabSslError(tab: BrowserTab, url: String) {
        tab.isLoading = false
        showMessage(app.getString(R.string.ssl_error))
    }

    override fun tabPageError(tab: BrowserTab, description: String) {
        tab.isLoading = false
    }

    override fun tabFileChooser(
        tab: BrowserTab,
        callback: ValueCallback<Array<Uri>>,
        params: WebChromeClient.FileChooserParams,
    ) {
        fileChooserCallback?.onReceiveValue(null)
        fileChooserCallback = callback
        pendingFileChooser = params
    }

    override fun tabCreateWindow(tab: BrowserTab, resultMsg: Message?): Boolean {
        val message = resultMsg ?: return false
        val transport = message.obj as? WebView.WebViewTransport ?: return false
        val popup = createTab(incognito = tab.isIncognito)
        transport.webView = popup.webView
        message.sendToTarget()
        return true
    }

    override fun tabShouldOverrideUrl(context: Context, url: String): Boolean {
        val lower = url.lowercase()
        if (lower.startsWith("http://") ||
            lower.startsWith("https://") ||
            lower.startsWith("about:") ||
            lower.startsWith("data:") ||
            lower.startsWith("blob:") ||
            lower.startsWith("javascript:")
        ) {
            return false
        }

        val scheme = lower.substringBefore(':')
        if (scheme !in ALLOWED_EXTERNAL_SCHEMES) return true

        return try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            true
        } catch (error: Exception) {
            showMessage(app.getString(R.string.external_open_failed))
            true
        }
    }

    fun onFileChooserResult(uris: Array<Uri>?) {
        fileChooserCallback?.onReceiveValue(uris)
        fileChooserCallback = null
        pendingFileChooser = null
    }

    // ------------------------------------------------------------- lifecycle

    fun onForeground() {
        activeTab?.webView?.onResume()
    }

    fun onBackground() {
        tabs.forEach { tab -> tab.webView?.onPause() }
    }

    override fun onCleared() {
        super.onCleared()
        host?.removeAllViews()
        tabs.forEach { tab ->
            tab.webView?.let { webView ->
                tab.webView = null
                webView.stopLoading()
                webView.destroy()
            }
        }
        tabs.clear()
        host = null
    }

    private companion object {
        val ALLOWED_EXTERNAL_SCHEMES = setOf("mailto", "tel", "sms", "smsto", "geo", "market")
        val WebViewClientNoop = android.webkit.WebViewClient()
    }
}
