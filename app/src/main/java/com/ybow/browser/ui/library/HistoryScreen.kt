package com.ybow.browser.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.ybow.browser.data.local.HistoryEntry
import com.ybow.browser.ui.components.EmptyState
import com.ybow.browser.ui.components.FaviconBadge
import com.ybow.browser.ui.components.YbowListRow
import com.ybow.browser.ui.components.YbowSearchField
import com.ybow.browser.ui.components.YbowTopBar
import com.ybow.browser.util.UrlTools
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun HistoryScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
) {
    val history by viewModel.history.collectAsStateWithLifecycle(emptyList())
    var query by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    val filtered = remember(history, query) {
        if (query.isBlank()) {
            history
        } else {
            history.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.url.contains(query, ignoreCase = true)
            }
        }
    }

    val todayLabel = stringResource(R.string.history_today)
    val yesterdayLabel = stringResource(R.string.history_yesterday)
    val earlierLabel = stringResource(R.string.history_earlier)

    val groups = remember(filtered, todayLabel, yesterdayLabel, earlierLabel) {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val yesterday = today.minusDays(1)
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        filtered.groupBy { entry ->
            val date = Instant.ofEpochMilli(entry.visitedAt).atZone(zone).toLocalDate()
            when (date) {
                today -> todayLabel
                yesterday -> yesterdayLabel
                else -> formatter.format(date)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        YbowTopBar(
            title = stringResource(R.string.history_title),
            subtitle = filtered.size.toString(),
            onBack = onBack,
            actions = {
                if (history.isNotEmpty()) {
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = stringResource(R.string.history_clear_confirm_title),
                        )
                    }
                }
            },
        )

        YbowSearchField(
            query = query,
            onQueryChange = { query = it },
            hint = stringResource(R.string.history_title),
        )

        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.History,
                title = stringResource(R.string.history_empty),
                description = stringResource(R.string.history_clear_confirm_body),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                groups.forEach { (label, entries) ->
                    item(key = "header-$label") {
                        Text(
                            text = label.ifBlank { earlierLabel },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
                        )
                    }
                    items(entries.size, key = { index -> entries[index].id }) { index ->
                        val entry: HistoryEntry = entries[index]
                        YbowListRow(
                            title = entry.title,
                            subtitle = UrlTools.displayUrl(entry.url),
                            leading = { FaviconBadge(url = entry.url) },
                            onClick = { onOpen(entry.url) },
                            trailing = {
                                IconButton(onClick = { viewModel.deleteHistoryEntry(entry) }) {
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

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.history_clear_confirm_title)) },
            text = { Text(stringResource(R.string.history_clear_confirm_body)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearDialog = false
                }) {
                    Text(stringResource(R.string.action_clear))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.action_close))
                }
            },
        )
    }
}
