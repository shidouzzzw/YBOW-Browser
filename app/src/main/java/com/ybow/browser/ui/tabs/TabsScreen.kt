package com.ybow.browser.ui.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ybow.browser.R
import com.ybow.browser.browser.BrowserTab
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.ui.components.EmptyState
import com.ybow.browser.ui.components.FaviconBadge
import com.ybow.browser.ui.components.YbowTopBar

@Composable
fun TabsScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit,
) {
    val tabs = viewModel.tabs.toList()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        YbowTopBar(
            title = stringResource(R.string.tabs_title),
            subtitle = stringResource(R.string.tabs_count, tabs.size),
            onBack = onBack,
            actions = {
                Surface(
                    onClick = {
                        viewModel.openNewTab()
                        onBack()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = colors.primary,
                    modifier = Modifier.height(40.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.menu_new_tab),
                            tint = colors.onPrimary,
                            modifier = Modifier.size(17.dp),
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = stringResource(R.string.menu_new_tab),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.onPrimary,
                        )
                    }
                }
            },
        )

        if (tabs.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Tab,
                title = stringResource(R.string.tabs_title),
                description = stringResource(R.string.tabs_empty),
                modifier = Modifier.padding(top = 40.dp),
            )
            return@Column
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 26.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
        ) {
            items(tabs, key = { it.id }) { tab ->
                TabCard(
                    tab = tab,
                    isActive = tab.id == viewModel.activeTabId,
                    onClick = {
                        viewModel.selectTab(tab.id)
                        onBack()
                    },
                    onClose = { viewModel.closeTab(tab.id) },
                )
            }
        }
    }
}

@Composable
private fun TabCard(
    tab: BrowserTab,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = colors.surface,
        border = BorderStroke(
            if (isActive) 1.5.dp else 1.dp,
            if (isActive) colors.primary.copy(alpha = 0.55f) else colors.outlineVariant.copy(alpha = 0.6f),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp),
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FaviconBadge(url = tab.url, favicon = tab.favicon, size = 28.dp)
                Spacer(Modifier.weight(1f))
                if (tab.isIncognito) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(colors.primary.copy(alpha = 0.14f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.tabs_incognito_badge),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.primary,
                        )
                    }
                }
                Surface(
                    onClick = onClose,
                    shape = RoundedCornerShape(10.dp),
                    color = colors.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(28.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.action_close),
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = tab.title.ifBlank {
                    stringResource(R.string.shortcut_new_tab_short)
                },
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 8.dp),
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = com.ybow.browser.util.UrlTools.displayUrl(tab.url),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .padding(end = 8.dp),
            )
        }
    }
}
