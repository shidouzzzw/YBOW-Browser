package com.ybow.browser.ui.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ybow.browser.R
import com.ybow.browser.ui.theme.YbowMotion
import com.ybow.browser.ui.theme.pressScale

/** Height of the floating pill itself. */
val BottomBarHeight = 62.dp

/** Space the scrollable content keeps free at its end so the pill never hides it. */
val BottomBarContentPadding = 96.dp

/**
 * Floating navigation pill: 16dp side margins, 12dp above the bottom inset, fully
 * rounded and softly shadowed. It has no strip or backing plate of its own, so page
 * content stays visible right up to its edge and keeps scrolling underneath.
 */
@Composable
fun BottomToolbar(
    canGoBack: Boolean,
    tabCount: Int,
    onTabs: () -> Unit,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onHome: () -> Unit,
    onMenu: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
    ) {
        Surface(
            shape = CircleShape,
            color = colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(BottomBarHeight)
                .shadow(8.dp, CircleShape),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TabsAction(
                    tabCount = tabCount,
                    onClick = onTabs,
                )
                BarAction(
                    icon = Icons.Rounded.ArrowBack,
                    label = stringResource(R.string.action_back),
                    enabled = canGoBack,
                    onClick = onBack,
                )
                SearchAction(onClick = onSearch)
                BarAction(
                    icon = Icons.Rounded.Home,
                    label = stringResource(R.string.action_home),
                    enabled = true,
                    onClick = onHome,
                )
                BarAction(
                    icon = Icons.Rounded.MoreHoriz,
                    label = stringResource(R.string.action_menu),
                    enabled = true,
                    onClick = onMenu,
                )
            }
        }
    }
}

@Composable
private fun RowScope.TabsAction(
    tabCount: Int,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val source = remember { MutableInteractionSource() }
    IconButton(
        onClick = onClick,
        interactionSource = source,
        modifier = Modifier
            .weight(1f)
            .pressScale(source, YbowMotion.BarPressScale),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Rounded.Tab,
                contentDescription = stringResource(R.string.action_tabs),
                tint = colors.onSurface,
                modifier = Modifier.size(23.dp),
            )
            Surface(
                shape = CircleShape,
                color = colors.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 2.dp),
            ) {
                Text(
                    text = tabCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                )
            }
        }
    }
}

@Composable
private fun RowScope.SearchAction(onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            onClick = onClick,
            interactionSource = source,
            shape = CircleShape,
            color = colors.primary,
            modifier = Modifier
                .size(46.dp)
                .pressScale(source, YbowMotion.BarPressScale),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.address_bar_hint),
                    tint = colors.onPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun RowScope.BarAction(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val source = remember { MutableInteractionSource() }
    IconButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = source,
        modifier = Modifier
            .weight(1f)
            .pressScale(source, YbowMotion.BarPressScale),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) {
                colors.onSurface
            } else {
                colors.onSurface.copy(alpha = 0.3f)
            },
            modifier = Modifier.size(22.dp),
        )
    }
}
