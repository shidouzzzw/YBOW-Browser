package com.ybow.browser.ui.browser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ybow.browser.R
import com.ybow.browser.ui.components.IconChip

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
    val colors = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 10.dp)
                .padding(bottom = 14.dp),
        ) {
            MenuRow(Icons.Rounded.Add, stringResource(R.string.menu_new_tab)) {
                onDismiss()
                onNewTab()
            }
            MenuRow(Icons.Rounded.VisibilityOff, stringResource(R.string.menu_new_incognito_tab)) {
                onDismiss()
                onIncognitoTab()
            }

            if (hasPage) {
                MenuDivider()
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

            MenuDivider()

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
        }
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        modifier = Modifier.padding(start = 66.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
    )
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconChip(icon = icon, size = 36.dp)
            Spacer(Modifier.width(14.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
