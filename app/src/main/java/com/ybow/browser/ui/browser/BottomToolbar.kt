package com.ybow.browser.ui.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreHoriz
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ybow.browser.R

/**
 * Floating navigation island. The bar is a translucent pill that hovers above the
 * page with generous side and bottom margins, so the browser feels like it sits on
 * a sheet of glass instead of a full-width system bar.
 */
@Composable
fun BottomToolbar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onTabs: () -> Unit,
    onMenu: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val glass = Brush.verticalGradient(
        listOf(
            colors.surface.copy(alpha = 0.97f),
            colors.surface.copy(alpha = 0.80f),
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(glass)
                .border(1.dp, colors.outlineVariant.copy(alpha = 0.55f), CircleShape)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BarAction(
                icon = Icons.Rounded.ArrowBack,
                label = stringResource(R.string.action_back),
                enabled = canGoBack,
                onClick = onBack,
            )
            BarAction(
                icon = Icons.Rounded.ArrowForward,
                label = stringResource(R.string.action_forward),
                enabled = canGoForward,
                onClick = onForward,
            )

            Surface(
                onClick = onTabs,
                shape = RoundedCornerShape(18.dp),
                color = colors.primary,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(42.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tab,
                        contentDescription = stringResource(R.string.action_tabs),
                        tint = colors.onPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = tabCount.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onPrimary,
                    )
                }
            }

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

@Composable
private fun RowScope.BarAction(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.weight(1f),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) {
                colors.onSurface
            } else {
                colors.onSurfaceVariant.copy(alpha = 0.32f)
            },
            modifier = Modifier.size(22.dp),
        )
    }
}
