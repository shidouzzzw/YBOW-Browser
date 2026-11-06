package com.ybow.browser.ui.browser

import android.content.Intent
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.ui.YbowScreen
import com.ybow.browser.ui.home.HomeScreen
import com.ybow.browser.ui.search.SearchScreen

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onNavigate: (YbowScreen) -> Unit,
) {
    val tab = viewModel.activeTab
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle(emptyList())
    val history by viewModel.history.collectAsStateWithLifecycle(emptyList())
    var menuOpen by remember { mutableStateOf(false) }
    var searchOpen by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val hasPage = tab?.hasPage == true

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

    // Top bar and body share one background colour, with nothing in between.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .imePadding(),
        ) {
            // The home screen has no top bar at all: its own search box is the only entry point.
            if (hasPage) {
                AddressBar(
                    tab = tab,
                    onOpenSearch = { searchOpen = true },
                    onReload = { viewModel.reload() },
                    onStop = { viewModel.stopLoading() },
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                WebViewHost(viewModel = viewModel)
                if (!hasPage) {
                    HomeScreen(
                        searchEngineLabel = settings.searchEngine.label,
                        bookmarks = bookmarks,
                        onOpenSearch = { searchOpen = true },
                        onOpenBookmark = { bookmark -> viewModel.openUrlInNewTab(bookmark.url) },
                        onShowBookmarks = { onNavigate(YbowScreen.Bookmarks) },
                        onIncognito = { viewModel.openNewTab(incognito = true) },
                    )
                }
            }
        }

        // The pill floats over the content: no strip, no backing plate underneath it.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        ) {
            BottomToolbar(
                canGoBack = tab?.canGoBack == true,
                tabCount = viewModel.tabs.size,
                onTabs = { onNavigate(YbowScreen.Tabs) },
                onBack = { viewModel.goBack() },
                onSearch = { searchOpen = true },
                onHome = { viewModel.goHome() },
                onMenu = { menuOpen = true },
            )
        }
    }

    AnimatedVisibility(
        visible = searchOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 6 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 6 }),
    ) {
        SearchScreen(
            history = history,
            bookmarks = bookmarks,
            searchEngineLabel = settings.searchEngine.label,
            onDismiss = { searchOpen = false },
            onSubmit = { text ->
                viewModel.navigate(text)
                searchOpen = false
            },
            onOpenUrl = { url ->
                viewModel.openUrlInNewTab(url)
                searchOpen = false
            },
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
