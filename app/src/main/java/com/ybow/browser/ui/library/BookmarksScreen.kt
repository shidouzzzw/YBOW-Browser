package com.ybow.browser.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.R
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.ui.components.EmptyState
import com.ybow.browser.ui.components.FaviconBadge
import com.ybow.browser.ui.components.YbowListRow
import com.ybow.browser.ui.components.YbowSearchField
import com.ybow.browser.ui.components.YbowTopBar
import com.ybow.browser.util.UrlTools

@Composable
fun BookmarksScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
) {
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle(emptyList())
    var query by remember { mutableStateOf("") }

    val filtered = remember(bookmarks, query) {
        if (query.isBlank()) {
            bookmarks
        } else {
            bookmarks.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.url.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        YbowTopBar(
            title = stringResource(R.string.bookmarks_title),
            subtitle = bookmarks.size.toString(),
            onBack = onBack,
        )

        YbowSearchField(
            query = query,
            onQueryChange = { query = it },
            hint = stringResource(R.string.bookmarks_title),
        )

        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.StarBorder,
                title = stringResource(R.string.bookmarks_empty),
                description = stringResource(R.string.bookmarks_empty_hint),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                items(filtered, key = { it.id }) { bookmark ->
                    YbowListRow(
                        title = bookmark.title,
                        subtitle = UrlTools.displayUrl(bookmark.url),
                        leading = { FaviconBadge(url = bookmark.url) },
                        onClick = { onOpen(bookmark.url) },
                        trailing = {
                            IconButton(onClick = { viewModel.removeBookmark(bookmark) }) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = stringResource(R.string.action_delete),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(start = 60.dp),
                    )
                }
            }
        }
    }
}
