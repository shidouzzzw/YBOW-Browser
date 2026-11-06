package com.ybow.browser.ui.browser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ybow.browser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserMenuSheet(
    hasPage: Boolean,
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    onNewTab: () -> Unit,
    onIncognitoTab: () -> Unit,
    onBookmarks: () -> Unit,
    onHistory: () -> Unit,
    onDownloads: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit,
    onSettings: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            MenuRow(Icons.Rounded.Add, stringResource(R.string.menu_new_tab)) {
                onDismiss()
                onNewTab()
            }
            MenuRow(Icons.Rounded.VisibilityOff, stringResource(R.string.menu_new_incognito_tab)) {
                onDismiss()
                onIncognitoTab()
            }

            if (hasPage) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                MenuRow(
                    icon = if (isBookmarked) Icons.Rounded.BookmarkRemove else Icons.Rounded.BookmarkAdd,
                    label = stringResource(
                        if (isBookmarked) R.string.menu_remove_bookmark else R.string.menu_add_bookmark,
                    ),
                ) {
                    onDismiss()
                    onToggleBookmark()
                }
                MenuRow(Icons.Rounded.Share, stringResource(R.string.menu_share)) {
                    onDismiss()
                    onShare()
                }
                MenuRow(Icons.Rounded.ContentCopy, stringResource(R.string.menu_copy)) {
                    onDismiss()
                    onCopy()
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            MenuRow(Icons.Rounded.StarBorder, stringResource(R.string.menu_bookmarks)) {
                onDismiss()
                onBookmarks()
            }
            MenuRow(Icons.Rounded.History, stringResource(R.string.menu_history)) {
                onDismiss()
                onHistory()
            }
            MenuRow(Icons.Rounded.Download, stringResource(R.string.menu_downloads)) {
                onDismiss()
                onDownloads()
            }
            MenuRow(Icons.Rounded.Settings, stringResource(R.string.menu_settings)) {
                onDismiss()
                onSettings()
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(21.dp),
        )
        Spacer(Modifier.width(18.dp))
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}
