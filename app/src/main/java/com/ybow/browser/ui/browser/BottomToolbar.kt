@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.ybow.browser.ui.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.combinedClickable
import com.ybow.browser.ui.theme.LocalYbowPalette
import com.ybow.browser.ui.theme.YbowIcons
import com.ybow.browser.ui.theme.YbowMotion
import com.ybow.browser.ui.theme.YbowPalette
import com.ybow.browser.ui.theme.pressScale

/** Height of the floating pill; content keeps this free at its end. */
val BottomBarHeight = 68.dp
val BottomBarContentPadding = 128.dp

/**
 * The floating pill from the design: tabs with a bordered counter, back, a raised
 * search button, home and menu. It hovers over the content with no backing strip.
 */
@Composable
fun BottomToolbar(
    tabCount: Int,
    canGoBack: Boolean,
    tabsOpen: Boolean,
    onTabs: () -> Unit,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onHome: () -> Unit,
    onMenu: () -> Unit,
) {
    val palette = LocalYbowPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BottomBarHeight)
                .shadow(18.dp, CircleShape)
                .clip(CircleShape)
                .background(palette.card.copy(alpha = 0.94f))
                .border(1.dp, palette.line, CircleShape)
                .padding(horizontal = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val source = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .pressScale(source, YbowMotion.PressPillButton)
                    .clip(CircleShape)
                    .background(
                        if (tabsOpen) palette.accent.copy(alpha = 0.20f) else androidx.compose.ui.graphics.Color.Transparent,
                    )
                    .combinedClickable(
                        interactionSource = source,
                        indication = null,
                        onClick = onTabs,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            width = 2.dp,
                            color = if (tabsOpen) palette.accent else palette.text,
                            shape = RoundedCornerShape(7.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tabCount.toString(),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp,
                        ),
                        color = if (tabsOpen) palette.accent else palette.text,
                    )
                }
            }

            PillButton(
                palette = palette,
                icon = YbowIcons.Back,
                enabled = canGoBack,
                onClick = onBack,
            )

            val searchSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .pressScale(searchSource, YbowMotion.PressPillButton)
                    .clip(CircleShape)
                    .background(palette.accent)
                    .combinedClickable(
                        interactionSource = searchSource,
                        indication = null,
                        onClick = onSearch,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = YbowIcons.Search,
                    contentDescription = "Ara",
                    tint = palette.bg,
                    modifier = Modifier.size(25.dp),
                )
            }

            PillButton(
                palette = palette,
                icon = YbowIcons.Home,
                enabled = true,
                onClick = onHome,
            )

            PillButton(
                palette = palette,
                icon = YbowIcons.Dots,
                enabled = true,
                onClick = onMenu,
            )
        }
    }
}

@Composable
private fun PillButton(
    palette: YbowPalette,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(48.dp)
            .pressScale(source, YbowMotion.PressPillButton)
            .clip(CircleShape)
            .combinedClickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.text.copy(alpha = if (enabled) 1f else 0.32f),
            modifier = Modifier.size(22.dp),
        )
    }
}
