package com.ybow.browser.ui.browser

import android.content.Intent
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.ui.YbowScreen
import com.ybow.browser.ui.home.HomeScreen

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onNavigate: (YbowScreen) -> Unit,
) {
    val tab = viewModel.activeTab
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle(emptyList())
    var menuOpen by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val fileChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        viewModel.onFileChooserResult(
            WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data),
        )
    }

    val pendingFileChooser = viewModel.pendingFileChooser
    LaunchedEffect(pendingFileChooser) {
        if (pendingFileChooser != null) {
            fileChooserLauncher.launch(pendingFileChooser.createIntent())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .imePadding(),
    ) {
        AddressBar(
            tab = tab,
            onNavigate = { input ->
                viewModel.navigate(input)
                focusManager.clearFocus()
            },
            onReload = { viewModel.reload() },
            onStop = { viewModel.stopLoading() },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            WebViewHost(viewModel = viewModel)
            if (tab == null || !tab.hasPage) {
                HomeScreen(
                    searchEngineLabel = settings.searchEngine.label,
                    bookmarks = bookmarks,
                    onSearch = { query ->
                        viewModel.navigate(query)
                        focusManager.clearFocus()
                    },
                    onOpenBookmark = { bookmark -> viewModel.openUrlInNewTab(bookmark.url) },
                    onShowBookmarks = { onNavigate(YbowScreen.Bookmarks) },
                    onIncognito = { viewModel.openNewTab(incognito = true) },
                )
            }
        }

        BottomToolbar(
            canGoBack = tab?.canGoBack == true,
            canGoForward = tab?.canGoForward == true,
            tabCount = viewModel.tabs.size,
            onBack = { viewModel.goBack() },
            onForward = { viewModel.goForward() },
            onHome = { viewModel.goHome() },
            onTabs = { onNavigate(YbowScreen.Tabs) },
            onMenu = { menuOpen = true },
        )
    }

    if (menuOpen) {
        val currentUrl = tab?.url.orEmpty()
        BrowserMenuSheet(
            hasPage = tab?.hasPage == true,
            isBookmarked = tab != null && bookmarks.any { it.url == tab.url },
            onDismiss = { menuOpen = false },
            onNewTab = { viewModel.openNewTab() },
            onIncognitoTab = { viewModel.openNewTab(incognito = true) },
            onBookmarks = { onNavigate(YbowScreen.Bookmarks) },
            onHistory = { onNavigate(YbowScreen.History) },
            onDownloads = { onNavigate(YbowScreen.Downloads) },
            onToggleBookmark = { tab?.let { viewModel.toggleBookmark(it) } },
            onShare = {
                if (currentUrl.isNotBlank()) {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, currentUrl)
                    }
                    context.startActivity(Intent.createChooser(send, null))
                }
            },
            onCopy = {
                if (currentUrl.isNotBlank()) {
                    clipboard.setText(AnnotatedString(currentUrl))
                    viewModel.notifyCopied()
                }
            },
            onSettings = { onNavigate(YbowScreen.Settings) },
        )
    }
}

@Composable
private fun WebViewHost(viewModel: BrowserViewModel) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            FrameLayout(context).also { viewModel.attachHost(it) }
        },
        update = { frame -> viewModel.attachHost(frame) },
    )
}
