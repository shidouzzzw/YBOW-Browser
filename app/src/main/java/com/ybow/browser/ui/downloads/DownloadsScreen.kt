package com.ybow.browser.ui.downloads

import android.app.DownloadManager
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.R
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.data.DownloadStatus
import com.ybow.browser.data.local.DownloadRecord
import com.ybow.browser.ui.components.EmptyState
import com.ybow.browser.ui.components.YbowTopBar
import kotlinx.coroutines.delay

@Composable
fun DownloadsScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit,
) {
    val records by viewModel.downloads.collectAsStateWithLifecycle(emptyList())
    var tick by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            tick += 1
        }
    }

    val statuses = remember(records, tick) {
        records.associate { record -> record.id to viewModel.downloadStatus(record.systemId) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        YbowTopBar(
            title = stringResource(R.string.downloads_title),
            subtitle = records.size.toString(),
            onBack = onBack,
        )

        if (records.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Download,
                title = stringResource(R.string.downloads_title),
                description = stringResource(R.string.downloads_empty),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                items(records, key = { it.id }) { record ->
                    DownloadRow(
                        record = record,
                        status = statuses[record.id],
                        onOpen = { viewModel.openDownload(record) },
                        onRemove = { viewModel.removeDownload(record) },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun DownloadRow(
    record: DownloadRecord,
    status: DownloadStatus?,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Download,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.fileName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = statusLabel(status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (status?.isSuccessful == true) {
                IconButton(onClick = onOpen) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = stringResource(R.string.action_open),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (status != null && status.isActive) {
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { status.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
private fun statusLabel(status: DownloadStatus?): String = when {
    status == null -> stringResource(R.string.downloads_unknown)
    status.state == DownloadManager.STATUS_SUCCESSFUL ->
        stringResource(R.string.downloads_state_successful)

    status.state == DownloadManager.STATUS_RUNNING ->
        stringResource(R.string.downloads_state_running)

    status.state == DownloadManager.STATUS_PAUSED ->
        stringResource(R.string.downloads_state_paused)

    status.state == DownloadManager.STATUS_PENDING ->
        stringResource(R.string.downloads_state_pending)

    else -> stringResource(R.string.downloads_state_failed)
}
