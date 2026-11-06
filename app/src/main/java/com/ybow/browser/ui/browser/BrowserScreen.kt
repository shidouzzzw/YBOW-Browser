@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.ybow.browser.ui.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.data.SearchEngine
import com.ybow.browser.data.local.Shortcut
import com.ybow.browser.ui.YbowScreen
import com.ybow.browser.ui.home.HomeScreen
import com.ybow.browser.ui.tabs.TabsScreen
import com.ybow.browser.ui.theme.LocalYbowPalette
import com.ybow.browser.ui.theme.YbowIcons
import com.ybow.browser.ui.theme.YbowMotion
import com.ybow.browser.ui.theme.YbowPalette
import com.ybow.browser.ui.theme.YbowTheme
import com.ybow.browser.ui.theme.pressScale
import com.ybow.browser.util.UrlTools
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The browser shell: a persistent WebView layer, the home surface above it, the tab
 * grid, the floating pill and the sheets. Colours follow the active tab, so an
 * incognito tab repaints the whole interface.
 */
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onNavigate: (YbowScreen) -> Unit,
) {
    val tab = viewModel.activeTab
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val shortcuts by viewModel.shortcuts.collectAsStateWithLifecycle(emptyList())
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle(emptyList())
    val palette = if (tab?.isIncognito == true) YbowPalette.Incognito else YbowPalette.Night

    YbowTheme(palette = palette) {
        val colors = LocalYbowPalette.current
        var tabsOpen by remember { mutableStateOf(false) }
        var sheet by remember { mutableStateOf<BrowserSheet>(BrowserSheet.None) }
        var toast by remember { mutableStateOf<String?>(null) }
        var searchFocusSignal by remember { mutableIntStateOf(0) }
        var freshUrl by remember { mutableStateOf<String?>(null) }
        var closingTabs by remember { mutableStateOf(setOf<String>()) }
        var visibleSheet by remember { mutableStateOf<BrowserSheet>(BrowserSheet.None) }
        val scope = rememberCoroutineScope()

        val hasPage = tab?.hasPage == true
        val isBookmarked = bookmarks.any { it.url == tab?.url }

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

        val snackbar = viewModel.snackbar
        LaunchedEffect(snackbar) {
            if (snackbar != null) {
                toast = snackbar
                viewModel.consumeSnackbar()
            }
        }
        LaunchedEffect(toast) {
            if (toast != null) {
                delay(YbowMotion.ToastMillis)
                toast = null
            }
        }
        LaunchedEffect(freshUrl) {
            if (freshUrl != null) {
                delay(YbowMotion.PopMillis + 200L)
                freshUrl = null
            }
        }
        LaunchedEffect(sheet) {
            if (sheet != BrowserSheet.None) visibleSheet = sheet
        }

        var hintShown by remember { mutableStateOf(false) }
        val shortcutCount = shortcuts.size
        LaunchedEffect(shortcutCount) {
            if (!hintShown && shortcutCount > 0) {
                delay(YbowMotion.LoadMillis.toLong())
                hintShown = true
                toast = "Kısayola uzun basarak düzenle ya da sil"
            }
        }

        androidx.activity.compose.BackHandler(enabled = sheet != BrowserSheet.None || tabsOpen || hasPage) {
            when {
                sheet != BrowserSheet.None -> sheet = BrowserSheet.None
                tabsOpen -> tabsOpen = false
                tab?.canGoBack == true -> viewModel.goBack()
                else -> viewModel.goHome(loadHomepage = false)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bg),
        ) {
            // ------------------------------ page layer (kept alive for the WebView)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                if (hasPage) {
                    PageBar(
                        palette = colors,
                        label = UrlTools.displayUrl(tab?.url),
                        secure = tab?.isSecure == true,
                        progress = (tab?.progress ?: 0) / 100f,
                        loading = tab?.isLoading == true,
                        onChip = { sheet = BrowserSheet.Search(tab?.url.orEmpty()) },
                        onReload = { viewModel.reload() },
                    )
                }
                WebViewHost(
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = BottomBarReservedSpace),
                )
            }

            // ------------------------------ home layer
            AnimatedVisibility(
                visible = !tabsOpen && !hasPage,
                enter = fadeIn(tween(YbowMotion.DurMillis)) +
                    scaleIn(
                        animationSpec = tween(YbowMotion.DurMillis, easing = YbowMotion.Ease),
                        initialScale = 0.985f,
                    ),
                exit = fadeOut(tween(YbowMotion.FastMillis)) +
                    scaleOut(
                        animationSpec = tween(YbowMotion.FastMillis, easing = YbowMotion.Ease),
                        targetScale = 0.985f,
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.bg)
                        .windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    HomeScreen(
                        shortcuts = shortcuts,
                        engineLabel = settings.searchEngine.label,
                        incognito = tab?.isIncognito == true,
                        onSearch = { text -> viewModel.navigate(text) },
                        onCycleEngine = {
                            val engines = SearchEngine.entries
                            val next = engines[(engines.indexOf(settings.searchEngine) + 1) % engines.size]
                            viewModel.setSearchEngine(next)
                        },
                        onOpenBookmarks = { onNavigate(YbowScreen.Bookmarks) },
                        onAddShortcut = { sheet = BrowserSheet.Edit(null) },
                        onOpenShortcut = { shortcut: Shortcut -> viewModel.navigate(shortcut.url) },
                        onHoldShortcut = { shortcut: Shortcut -> sheet = BrowserSheet.Item(shortcut) },
                        onToggleIncognito = {
                            if (tab?.isIncognito == true) {
                                viewModel.openNewTab()
                                toast = "Gizli mod kapalı"
                            } else {
                                viewModel.openNewTab(incognito = true)
                                toast = "Gizli mod açık"
                            }
                        },
                        focusSignal = searchFocusSignal,
                        freshShortcutUrl = freshUrl,
                    )
                }
            }

            // ------------------------------ tabs layer
            AnimatedVisibility(
                visible = tabsOpen,
                enter = fadeIn(tween(YbowMotion.DurMillis)) +
                    scaleIn(
                        animationSpec = tween(YbowMotion.DurMillis, easing = YbowMotion.Ease),
                        initialScale = 1.02f,
                    ),
                exit = fadeOut(tween(YbowMotion.FastMillis)) +
                    scaleOut(
                        animationSpec = tween(YbowMotion.FastMillis, easing = YbowMotion.Ease),
                        targetScale = 1.02f,
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.bg)
                        .windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    TabsScreen(
                        tabs = viewModel.tabs.toList(),
                        activeId = viewModel.activeTabId,
                        closingIds = closingTabs,
                        onSelect = { selected ->
                            viewModel.selectTab(selected.id)
                            tabsOpen = false
                        },
                        onClose = { closing ->
                            closingTabs = closingTabs + closing.id
                            scope.launch {
                                delay(YbowMotion.CloseTabMillis)
                                viewModel.closeTab(closing.id)
                                closingTabs = closingTabs - closing.id
                            }
                        },
                        onNewTab = {
                            viewModel.openNewTab()
                            tabsOpen = false
                            toast = "Yeni sekme"
                        },
                    )
                }
            }

            // ------------------------------ pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                BottomToolbar(
                    tabCount = viewModel.tabs.size,
                    canGoBack = sheet != BrowserSheet.None || tabsOpen || (tab?.canGoBack == true) || hasPage,
                    tabsOpen = tabsOpen,
                    onTabs = {
                        sheet = BrowserSheet.None
                        tabsOpen = !tabsOpen
                    },
                    onBack = {
                        when {
                            sheet != BrowserSheet.None -> sheet = BrowserSheet.None
                            tabsOpen -> tabsOpen = false
                            tab?.canGoBack == true -> viewModel.goBack()
                            hasPage -> viewModel.goHome(loadHomepage = false)
                            else -> toast = "Geri gidilecek sayfa yok"
                        }
                    },
                    onSearch = {
                        sheet = BrowserSheet.None
                        tabsOpen = false
                        if (hasPage) {
                            sheet = BrowserSheet.Search(tab?.url.orEmpty())
                        } else {
                            searchFocusSignal++
                        }
                    },
                    onHome = {
                        sheet = BrowserSheet.None
                        tabsOpen = false
                        viewModel.goHome(loadHomepage = false)
                    },
                    onMenu = { sheet = BrowserSheet.Menu },
                )
            }

            // ------------------------------ sheets
            SheetScrim(visible = sheet != BrowserSheet.None) { sheet = BrowserSheet.None }

            AnimatedVisibility(
                visible = sheet != BrowserSheet.None,
                enter = slideInVertically(
                    animationSpec = tween(YbowMotion.SlowMillis, easing = YbowMotion.EaseOut),
                    initialOffsetY = { it },
                ) + fadeIn(tween(180)),
                exit = slideOutVertically(
                    animationSpec = tween(200, easing = androidx.compose.animation.core.FastOutLinearInEasing),
                    targetOffsetY = { it },
                ) + fadeOut(tween(160)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                when (val current = visibleSheet) {
                    is BrowserSheet.Search -> OmniboxSheet(
                        initialUrl = current.initialUrl,
                        onDismiss = { sheet = BrowserSheet.None },
                        onSubmit = { input ->
                            sheet = BrowserSheet.None
                            viewModel.navigate(input)
                        },
                    )

                    is BrowserSheet.Edit -> ShortcutSheet(
                        shortcut = current.shortcut,
                        onDismiss = { sheet = BrowserSheet.None },
                        onSave = { host, name ->
                            val target = current.shortcut
                            val url = "https://$host"
                            if (target == null) {
                                viewModel.addShortcut(url, name)
                                freshUrl = url
                            } else {
                                viewModel.updateShortcut(target, url, name)
                            }
                            sheet = BrowserSheet.None
                        },
                    )

                    is BrowserSheet.Item -> ShortcutOptionsSheet(
                        shortcut = current.shortcut,
                        onDismiss = { sheet = BrowserSheet.None },
                        onOpen = {
                            sheet = BrowserSheet.None
                            viewModel.navigate(current.shortcut.url)
                        },
                        onEdit = {
                            val editing = current.shortcut
                            sheet = BrowserSheet.None
                            scope.launch {
                                delay(YbowMotion.SlowMillis.toLong())
                                sheet = BrowserSheet.Edit(editing)
                            }
                        },
                        onDelete = {
                            sheet = BrowserSheet.None
                            viewModel.deleteShortcut(current.shortcut)
                        },
                    )

                    is BrowserSheet.Menu -> MenuSheet(
                        hasPage = hasPage,
                        isBookmarked = isBookmarked,
                        onDismiss = { sheet = BrowserSheet.None },
                        onNewTab = {
                            sheet = BrowserSheet.None
                            viewModel.openNewTab()
                            toast = "Yeni sekme"
                        },
                        onIncognitoTab = {
                            sheet = BrowserSheet.None
                            viewModel.openNewTab(incognito = true)
                            toast = "Yeni gizli sekme"
                        },
                        onBookmarks = {
                            sheet = BrowserSheet.None
                            onNavigate(YbowScreen.Bookmarks)
                        },
                        onHistory = {
                            sheet = BrowserSheet.None
                            onNavigate(YbowScreen.History)
                        },
                        onDownloads = {
                            sheet = BrowserSheet.None
                            onNavigate(YbowScreen.Downloads)
                        },
                        onSettings = {
                            sheet = BrowserSheet.None
                            onNavigate(YbowScreen.Settings)
                        },
                        onToggleBookmark = {
                            sheet = BrowserSheet.None
                            tab?.let { viewModel.toggleBookmark(it) }
                        },
                        onShare = {
                            sheet = BrowserSheet.None
                            val u = tab?.url.orEmpty()
                            if (u.isNotBlank()) {
                                val send = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, u)
                                }
                                runCatching { context.startActivity(Intent.createChooser(send, "Paylaş")) }
                            }
                        },
                        onCopy = {
                            sheet = BrowserSheet.None
                            val u = tab?.url.orEmpty()
                            if (u.isNotBlank()) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("URL", u))
                                toast = "Bağlantı kopyalandı"
                            }
                        },
                    )

                    BrowserSheet.None -> Unit
                }
            }

            ToastBanner(
                text = toast,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 98.dp),
            )
        }
    }
}

@Composable
private fun PageBar(
    palette: YbowPalette,
    label: String,
    secure: Boolean,
    progress: Float,
    loading: Boolean,
    onChip: () -> Unit,
    onReload: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val reloadSource = remember { MutableInteractionSource() }
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(YbowMotion.DurMillis),
        label = "pageProgress",
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(CircleShape)
                    .background(palette.card)
                    .border(1.dp, palette.line, CircleShape)
                    .combinedClickable(
                        interactionSource = source,
                        indication = null,
                        onClick = onChip,
                    )
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            if (secure) palette.accent.copy(alpha = 0.14f) else palette.pink.copy(alpha = 0.14f),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = YbowIcons.Lock,
                        contentDescription = null,
                        tint = if (secure) palette.accent else palette.pink,
                        modifier = Modifier.size(13.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = label,
                    style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Normal),
                    color = palette.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .pressScale(reloadSource, YbowMotion.PressReload, rotate = 180f, highlight = true)
                    .clip(CircleShape)
                    .background(palette.card)
                    .border(1.dp, palette.line, CircleShape)
                    .combinedClickable(
                        interactionSource = reloadSource,
                        indication = null,
                        onClick = onReload,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = YbowIcons.Reload,
                    contentDescription = "Yenile",
                    tint = palette.text,
                    modifier = Modifier.size(19.dp),
                )
            }
        }

        if (loading) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(animatedProgress)
                    .height(3.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(palette.accent),
            )
        }
    }
}

@Composable
private fun WebViewHost(viewModel: BrowserViewModel, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            FrameLayout(context).also { viewModel.attachHost(it) }
        },
        update = { frame -> viewModel.attachHost(frame) },
    )
}
