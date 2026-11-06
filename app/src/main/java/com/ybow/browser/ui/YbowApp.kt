package com.ybow.browser.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.ui.browser.BrowserScreen
import com.ybow.browser.ui.downloads.DownloadsScreen
import com.ybow.browser.ui.library.BookmarksScreen
import com.ybow.browser.ui.library.HistoryScreen
import com.ybow.browser.ui.settings.SettingsScreen
import com.ybow.browser.ui.tabs.TabsScreen
import com.ybow.browser.ui.theme.YbowMotion

enum class YbowScreen {
    Browser, Tabs, Bookmarks, History, Downloads, Settings,
}

@Composable
fun YbowApp(viewModel: BrowserViewModel) {
    var screen by rememberSaveable { mutableStateOf(YbowScreen.Browser) }
    var lastOverlay by remember { mutableStateOf(YbowScreen.Tabs) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(screen) {
        if (screen != YbowScreen.Browser) lastOverlay = screen
    }

    val message = viewModel.snackbar
    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeSnackbar()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // The browser layer is never removed so live WebViews keep their state.
        BrowserScreen(
            viewModel = viewModel,
            onNavigate = { screen = it },
        )

        AnimatedVisibility(
            visible = screen != YbowScreen.Browser,
            enter = fadeIn(
                animationSpec = tween(YbowMotion.PageTransitionMillis, easing = YbowMotion.Enter),
            ) + slideInVertically(
                animationSpec = tween(YbowMotion.PageTransitionMillis, easing = YbowMotion.Enter),
                initialOffsetY = { it / YbowMotion.PageSlideDivisor },
            ),
            exit = fadeOut(
                animationSpec = tween(YbowMotion.PageTransitionMillis, easing = YbowMotion.Exit),
            ) + slideOutVertically(
                animationSpec = tween(YbowMotion.PageTransitionMillis, easing = YbowMotion.Exit),
                targetOffsetY = { it / YbowMotion.PageSlideDivisor },
            ),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                val close: () -> Unit = { screen = YbowScreen.Browser }
                when (lastOverlay) {
                    YbowScreen.Tabs -> TabsScreen(
                        viewModel = viewModel,
                        onBack = close,
                    )

                    YbowScreen.Bookmarks -> BookmarksScreen(
                        viewModel = viewModel,
                        onBack = close,
                        onOpen = { url ->
                            viewModel.openUrlInNewTab(url)
                            close()
                        },
                    )

                    YbowScreen.History -> HistoryScreen(
                        viewModel = viewModel,
                        onBack = close,
                        onOpen = { url ->
                            viewModel.openUrlInNewTab(url)
                            close()
                        },
                    )

                    YbowScreen.Downloads -> DownloadsScreen(
                        viewModel = viewModel,
                        onBack = close,
                    )

                    YbowScreen.Settings -> SettingsScreen(
                        viewModel = viewModel,
                        onBack = close,
                    )

                    YbowScreen.Browser -> Unit
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 72.dp, start = 12.dp, end = 12.dp),
        )
    }
}
