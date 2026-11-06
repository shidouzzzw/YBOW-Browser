package com.ybow.browser.ui.tabs

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
import androidx.compose.material3.IconButton
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
                IconButton(onClick = {
                    viewModel.openNewTab()
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.menu_new_tab),
                    )
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
            contentPadding = PaddingValues(14.dp),
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
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FaviconBadge(url = tab.url, favicon = tab.favicon, size = 26.dp)
                Spacer(Modifier.weight(1f))
                if (tab.isIncognito) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.tabs_incognito_badge),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = tab.title.ifBlank {
                    stringResource(R.string.shortcut_new_tab_short)
                },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 8.dp),
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = com.ybow.browser.util.UrlTools.displayUrl(tab.url),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .padding(end = 8.dp),
            )
        }
    }
}
