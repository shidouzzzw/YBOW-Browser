package com.ybow.browser.ui.browser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.sp
import com.ybow.browser.R
import com.ybow.browser.browser.BrowserTab
import com.ybow.browser.util.UrlTools

/**
 * Compact omnibox. Tapping it opens the dedicated search screen instead of editing
 * in place, so the keyboard never covers the page.
 */
@Composable
fun AddressBar(
    tab: BrowserTab?,
    onOpenSearch: () -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val displayUrl = UrlTools.displayUrl(tab?.url)
    val showHomeIcon = tab == null || !tab.hasPage
    val secure = tab?.isSecure == true

    val icon = when {
        showHomeIcon -> Icons.Rounded.Search
        secure -> Icons.Rounded.Lock
        else -> Icons.Rounded.WarningAmber
    }
    val iconTint = when {
        showHomeIcon -> colors.primary
        secure -> colors.primary
        else -> colors.secondary
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onOpenSearch,
                shape = RoundedCornerShape(26.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.8f)),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (showHomeIcon) stringResource(R.string.address_bar_hint) else displayUrl,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        color = if (showHomeIcon) colors.onSurfaceVariant else colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (tab?.isLoading == true) {
                        IconButton(onClick = onStop, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.action_stop),
                                tint = colors.onSurfaceVariant,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                onClick = onReload,
                shape = CircleShape,
                color = colors.surface,
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.8f)),
                shadowElevation = 6.dp,
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.action_reload),
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(19.dp),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (tab?.isLoading == true) {
                LinearProgressIndicator(
                    progress = { (tab.progress.coerceIn(0, 100)) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape),
                    color = colors.primary,
                    trackColor = colors.primary.copy(alpha = 0.12f),
                )
            }
        }
    }
}
