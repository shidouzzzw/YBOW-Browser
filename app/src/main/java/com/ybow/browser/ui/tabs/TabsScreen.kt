@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.ybow.browser.ui.tabs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ybow.browser.browser.BrowserTab
import com.ybow.browser.ui.theme.LocalYbowPalette
import com.ybow.browser.ui.theme.YbowColors
import com.ybow.browser.ui.theme.YbowIcons
import com.ybow.browser.ui.theme.YbowMotion
import com.ybow.browser.ui.theme.YbowPalette
import com.ybow.browser.ui.theme.pressScale
import com.ybow.browser.ui.theme.rise
import com.ybow.browser.ui.theme.siteIconBackground
import com.ybow.browser.ui.theme.siteIconForeground
import com.ybow.browser.util.UrlTools

/**
 * Tabs as a two column card grid, with a dashed "new tab" card, matching the design.
 */
@Composable
fun TabsScreen(
    tabs: List<BrowserTab>,
    activeId: String?,
    closingIds: Set<String>,
    onSelect: (BrowserTab) -> Unit,
    onClose: (BrowserTab) -> Unit,
    onNewTab: () -> Unit,
) {
    val palette = LocalYbowPalette.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.bg)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 128.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "Sekmeler",
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp),
                color = palette.text,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${tabs.size} açık",
                style = TextStyle(fontSize = 14.sp),
                color = palette.muted,
            )
        }

        Spacer(Modifier.height(18.dp))

        tabs.chunked(2).forEachIndexed { rowIndex, rowTabs ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .rise(rowIndex),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowTabs.forEach { tab ->
                    TabCard(
                        tab = tab,
                        isActive = tab.id == activeId,
                        closing = tab.id in closingIds,
                        modifier = Modifier.weight(1f),
                        onSelect = { onSelect(tab) },
                        onClose = { onClose(tab) },
                    )
                }
                repeat(2 - rowTabs.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        NewTabCard(palette = palette, onClick = onNewTab)
    }
}

@Composable
private fun TabCard(
    tab: BrowserTab,
    isActive: Boolean,
    closing: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalYbowPalette.current
    val source = remember { MutableInteractionSource() }
    val exit by animateFloatAsState(
        targetValue = if (closing) 1f else 0f,
        animationSpec = tween(YbowMotion.CloseTabMillis.toInt(), easing = YbowMotion.Ease),
        label = "tabExit",
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = 1f - 0.25f * exit
                scaleY = 1f - 0.25f * exit
                alpha = 1f - exit
            }
            .pressScale(source, YbowMotion.PressTabCard, highlight = true)
            .clip(RoundedCornerShape(20.dp))
            .background(palette.card)
            .border(
                width = if (isActive) 1.6.dp else 1.dp,
                color = if (isActive) palette.accent else palette.line,
                shape = RoundedCornerShape(20.dp),
            )
            .combinedClickable(
                interactionSource = source,
                indication = null,
                onClick = onSelect,
            )
            .padding(9.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
                .clip(RoundedCornerShape(13.dp))
                .then(
                    if (tab.hasPage) {
                        Modifier.background(palette.bg)
                    } else {
                        Modifier.background(
                            Brush.linearGradient(
                                listOf(
                                    if (tab.isIncognito) {
                                        YbowColors.LogoPink.copy(alpha = 0.12f)
                                    } else {
                                        palette.accent.copy(alpha = 0.10f)
                                    },
                                    palette.bg,
                                ),
                            ),
                        )
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                tab.hasPage -> {
                    val host = UrlTools.hostOf(tab.url)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(siteIconBackground(host)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = tab.title.take(1).uppercase().ifBlank { "?" },
                            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                            color = siteIconForeground(host),
                        )
                    }
                }

                else -> {
                    Icon(
                        imageVector = YbowIcons.Home,
                        contentDescription = null,
                        tint = if (tab.isIncognito) YbowColors.LogoPink else palette.accent,
                        modifier = Modifier.size(34.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 9.dp, start = 3.dp, end = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (tab.hasPage) {
                    tab.title.ifBlank { UrlTools.displayUrl(tab.url) }
                } else {
                    "Ana ekran"
                },
                style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold),
                color = palette.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            val closeSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .pressScale(closeSource, YbowMotion.PressPillButton)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.40f))
                    .combinedClickable(
                        interactionSource = closeSource,
                        indication = null,
                        onClick = onClose,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = YbowIcons.Close,
                    contentDescription = "Sekmeyi kapat",
                    tint = palette.text,
                    modifier = Modifier.size(13.dp),
                )
            }
        }

        Text(
            text = when {
                tab.hasPage -> UrlTools.displayUrl(tab.url)
                tab.isIncognito -> "Gizli sekme · ybow://ana"
                else -> "ybow://ana"
            },
            style = TextStyle(fontSize = 11.5.sp),
            color = if (tab.isIncognito) palette.pink else palette.muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp, start = 3.dp, end = 3.dp),
        )
    }
}

@Composable
private fun NewTabCard(palette: YbowPalette, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .pressScale(source, YbowMotion.PressNewTab, highlight = true)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, palette.line, RoundedCornerShape(20.dp))
            .combinedClickable(
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = YbowIcons.Plus,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Yeni sekme",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = palette.accent,
            )
        }
    }
}
