@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.ybow.browser.ui.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ybow.browser.ui.theme.LocalYbowPalette
import com.ybow.browser.ui.theme.YbowIcons
import com.ybow.browser.ui.theme.YbowMotion
import com.ybow.browser.ui.theme.pressScale

/** Height of the floating pill. */
val BottomBarHeight = 68.dp

/** Bottom space reserved for page content so nothing is hidden under the pill. */
val BottomBarReservedSpace = 78.dp

/**
 * Floating navigation pill: five equal-width columns so every touch target is large
 * and there are no dead gaps between buttons. Tabs counter with rounded badge, back,
 * raised gradient search FAB, home and menu.
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
            .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BottomBarHeight)
                .shadow(
                    elevation = 18.dp,
                    shape = CircleShape,
                    spotColor = palette.accent.copy(alpha = 0.28f),
                    ambientColor = Color.Black.copy(alpha = 0.45f),
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            palette.card.copy(alpha = 0.98f),
                            palette.bg.copy(alpha = 0.98f),
                        ),
                    ),
                )
                .border(
                    width = 1.dp,
                    color = if (tabsOpen) palette.accent.copy(alpha = 0.60f) else palette.line,
                    shape = CircleShape,
                )
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PillSlot(
                modifier = Modifier.weight(1f),
                onClick = onTabs,
                highlight = tabsOpen,
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (tabsOpen) palette.accent else Color.Transparent)
                        .border(
                            width = 1.8.dp,
                            color = palette.accent,
                            shape = RoundedCornerShape(8.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (tabCount > 99) "99+" else tabCount.toString(),
                        style = TextStyle(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp,
                        ),
                        color = if (tabsOpen) palette.bg else palette.accent,
                    )
                }
            }

            PillSlot(
                modifier = Modifier.weight(1f),
                onClick = onBack,
                enabled = canGoBack,
            ) {
                Icon(
                    imageVector = YbowIcons.Back,
                    contentDescription = "Geri",
                    tint = palette.text.copy(alpha = if (canGoBack) 1f else 0.30f),
                    modifier = Modifier.size(22.dp),
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                val searchSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .pressScale(searchSource, YbowMotion.PressGo, highlight = true)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    palette.accent,
                                    palette.accent.copy(alpha = 0.82f),
                                ),
                            ),
                        )
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
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            PillSlot(
                modifier = Modifier.weight(1f),
                onClick = onHome,
            ) {
                Icon(
                    imageVector = YbowIcons.Home,
                    contentDescription = "Başlangıç",
                    tint = palette.text,
                    modifier = Modifier.size(22.dp),
                )
            }

            PillSlot(
                modifier = Modifier.weight(1f),
                onClick = onMenu,
            ) {
                Icon(
                    imageVector = YbowIcons.Dots,
                    contentDescription = "Menü",
                    tint = palette.text,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun PillSlot(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlight: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val palette = LocalYbowPalette.current
    Box(
        modifier = modifier
            .height(BottomBarHeight)
            .pressScale(source, YbowMotion.PressPillButton, highlight = true)
            .clip(CircleShape)
            .background(
                if (highlight) palette.accent.copy(alpha = 0.16f) else Color.Transparent,
            )
            .combinedClickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
