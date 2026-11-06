@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.ybow.browser.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ybow.browser.R
import com.ybow.browser.data.local.Shortcut
import com.ybow.browser.ui.theme.LocalYbowPalette
import com.ybow.browser.ui.theme.TextWordmark
import com.ybow.browser.ui.theme.YbowColors
import com.ybow.browser.ui.theme.YbowIcons
import com.ybow.browser.ui.theme.YbowMotion
import com.ybow.browser.ui.theme.pressScale
import com.ybow.browser.ui.theme.rise
import com.ybow.browser.ui.theme.siteIconBackground
import com.ybow.browser.ui.theme.siteIconForeground
import com.ybow.browser.util.UrlTools

/**
 * Home screen: brand block with animated wave logo, focusable search pill,
 * engine switcher, quick access shortcut grid and incognito card.
 */
@Composable
fun HomeScreen(
    shortcuts: List<Shortcut>,
    engineLabel: String,
    incognito: Boolean,
    onSearch: (String) -> Unit,
    onCycleEngine: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onAddShortcut: () -> Unit,
    onOpenShortcut: (Shortcut) -> Unit,
    onHoldShortcut: (Shortcut) -> Unit,
    onToggleIncognito: () -> Unit,
    focusSignal: Int,
    freshShortcutUrl: String?,
) {
    val palette = LocalYbowPalette.current
    val scroll = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val searchFocus = remember { FocusRequester() }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(focusSignal) {
        if (focusSignal > 0) {
            kotlinx.coroutines.delay(YbowMotion.FocusDelayMillis)
            runCatching { searchFocus.requestFocus() }
            scroll.animateScrollTo(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.bg)
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 128.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandBlock(palette = palette, incognito = incognito, modifier = Modifier.rise(0))

        Spacer(Modifier.height(30.dp))

        SearchPill(
            palette = palette,
            query = query,
            onQueryChange = { query = it },
            focusRequester = searchFocus,
            onSubmit = {
                val text = query.trim()
                if (text.isNotEmpty()) {
                    keyboard?.hide()
                    focusManager.clearFocus()
                    query = ""
                    onSearch(text)
                }
            },
            modifier = Modifier.rise(2),
        )

        EngineRow(
            palette = palette,
            engineLabel = engineLabel,
            onCycle = onCycleEngine,
            modifier = Modifier
                .fillMaxWidth()
                .rise(3),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, bottom = 14.dp)
                .rise(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_quick_access),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                ),
                color = palette.text,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LinkText(text = stringResource(R.string.bookmarks_title), onClick = onOpenBookmarks)
                AddButton(palette = palette, onClick = onAddShortcut)
            }
        }

        if (shortcuts.isEmpty()) {
            EmptyShortcuts(palette = palette)
        } else {
            shortcuts.chunked(4).forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .rise(5),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    rowItems.forEach { shortcut ->
                        ShortcutTile(
                            shortcut = shortcut,
                            fresh = shortcut.url == freshShortcutUrl,
                            modifier = Modifier.weight(1f),
                            onOpen = { onOpenShortcut(shortcut) },
                            onHold = { onHoldShortcut(shortcut) },
                        )
                    }
                    repeat(4 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        IncognitoCard(
            palette = palette,
            incognito = incognito,
            onToggle = onToggleIncognito,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
                .rise(6),
        )
    }
}

@Composable
private fun BrandBlock(
    palette: com.ybow.browser.ui.theme.YbowPalette,
    incognito: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WaveLogo(incognito = incognito)
        Spacer(Modifier.height(24.dp))
        Text(
            text = "YBOW",
            style = TextWordmark,
            color = palette.text,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (incognito) {
                "Sessiz, hızlı, iz bırakmaz."
            } else {
                "Sessiz, hızlı, senin."
            },
            style = TextStyle(fontSize = 15.5.sp),
            color = palette.muted,
        )
    }
}

/** Rounded gradient tile with three endlessly drifting wave lines. */
@Composable
private fun WaveLogo(incognito: Boolean) {
    val transition = rememberInfiniteTransition(label = "logoWave")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart,
        ),
        label = "waveShift",
    )
    val start = if (incognito) YbowColors.LogoPink else YbowColors.LogoBlue
    val end = if (incognito) YbowColors.LogoBlue else YbowColors.LogoPink

    Canvas(
        modifier = Modifier
            .size(92.dp)
            .clip(RoundedCornerShape(28.dp)),
    ) {
        drawRect(brush = Brush.linearGradient(listOf(start, end)))
        val unit = size.width / 64f
        val waveClip = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 14f * unit,
                    top = 14f * unit,
                    right = 50f * unit,
                    bottom = 50f * unit,
                    radiusX = 4f * unit,
                    radiusY = 4f * unit,
                ),
            )
        }
        clipPath(waveClip) {
            for (row in 0 until 3) {
                val baseY = (22f + row * 10f) * unit
                val path = Path().apply {
                    var x = -18f * unit - 18f * unit + shift * unit
                    moveTo(x, baseY)
                    while (x < size.width + 18f * unit) {
                        quadraticTo(
                            x + 4.5f * unit,
                            baseY - 5f * unit,
                            x + 9f * unit,
                            baseY,
                        )
                        quadraticTo(
                            x + 13.5f * unit,
                            baseY + 5f * unit,
                            x + 18f * unit,
                            baseY,
                        )
                        x += 18f * unit
                    }
                }
                drawPath(
                    path = path,
                    color = YbowColors.Background,
                    style = Stroke(width = 3.4f * unit, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                )
            }
        }
    }
}

@Composable
private fun SearchPill(
    palette: com.ybow.browser.ui.theme.YbowPalette,
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val accent = palette.accent
    val borderColor by animateColorAsState(
        targetValue = if (focused) accent else palette.line,
        animationSpec = tween(YbowMotion.FastMillis),
        label = "searchBorder",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (focused) palette.cardRaised else palette.card,
                        palette.card,
                    ),
                ),
            )
            .border(
                width = if (focused) 1.5.dp else 1.dp,
                color = borderColor,
                shape = CircleShape,
            )
            .padding(start = 18.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = YbowIcons.Search,
            contentDescription = null,
            tint = if (focused) accent else palette.muted,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.5.sp, color = palette.text),
            cursorBrush = SolidColor(accent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
            interactionSource = interaction,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.address_bar_hint),
                            style = TextStyle(fontSize = 16.5.sp),
                            color = palette.muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    inner()
                }
            },
        )
        Spacer(Modifier.width(8.dp))
        GoButton(palette = palette, onClick = onSubmit)
    }
}

@Composable
private fun GoButton(palette: com.ybow.browser.ui.theme.YbowPalette, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(46.dp)
            .pressScale(source, YbowMotion.PressGo, highlight = true)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        palette.accent,
                        palette.accent.copy(alpha = 0.84f),
                    ),
                ),
            )
            .combinedClickable(
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = YbowIcons.Arrow,
            contentDescription = stringResource(R.string.action_go),
            tint = palette.bg,
            modifier = Modifier.size(21.dp),
        )
    }
}

@Composable
private fun EngineRow(
    palette: com.ybow.browser.ui.theme.YbowPalette,
    engineLabel: String,
    onCycle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(top = 10.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCycle,
                )
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Arama motoru: ", style = TextStyle(fontSize = 13.sp), color = palette.muted)
            Text(
                text = engineLabel,
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                color = palette.accent,
            )
        }
    }
}

@Composable
private fun LinkText(text: String, onClick: () -> Unit) {
    val palette = LocalYbowPalette.current
    Text(
        text = text,
        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
        color = palette.accent,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    )
}

@Composable
private fun AddButton(palette: com.ybow.browser.ui.theme.YbowPalette, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(30.dp)
            .pressScale(source, YbowMotion.PressAdd, rotate = 90f)
            .clip(CircleShape)
            .background(palette.cardRaised)
            .border(1.dp, palette.line, CircleShape)
            .combinedClickable(
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = YbowIcons.Plus,
            contentDescription = stringResource(R.string.shortcut_add),
            tint = palette.accent,
            modifier = Modifier.size(16.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShortcutTile(
    shortcut: Shortcut,
    fresh: Boolean,
    modifier: Modifier = Modifier,
    onOpen: () -> Unit,
    onHold: () -> Unit,
) {
    val palette = LocalYbowPalette.current
    val host = UrlTools.hostOf(shortcut.url)
    val tileSource = remember { MutableInteractionSource() }
    val pop by animateFloatAsState(
        targetValue = if (fresh) 1f else 0f,
        animationSpec = tween(YbowMotion.PopMillis, easing = YbowMotion.Spring),
        label = "pop",
    )

    Column(
        modifier = modifier
            .graphicsLayerAlphaPop(pop)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOpen,
                onLongClick = onHold,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .pressScale(tileSource, YbowMotion.PressTile)
                .clip(CircleShape)
                .background(siteIconBackground(host))
                .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = shortcut.title.take(1).uppercase(),
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = siteIconForeground(host),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = shortcut.title,
            style = TextStyle(fontSize = 12.sp),
            color = palette.muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EmptyShortcuts(palette: com.ybow.browser.ui.theme.YbowPalette) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.card)
            .dashedBorder(palette.line, 18.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = YbowIcons.Bookmark,
            contentDescription = null,
            tint = palette.muted,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Kısayol yok. Sağ üstteki + ile site ekle.",
            style = TextStyle(fontSize = 14.sp),
            color = palette.muted,
        )
    }
}

@Composable
private fun IncognitoCard(
    palette: com.ybow.browser.ui.theme.YbowPalette,
    incognito: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val source = remember { MutableInteractionSource() }
    val pink = palette.pink
    Row(
        modifier = modifier
            .pressScale(source, YbowMotion.PressCard, highlight = true)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        lerpColor(palette.card, pink, 0.10f),
                        palette.card,
                    ),
                ),
            )
            .border(1.dp, palette.line, RoundedCornerShape(22.dp))
            .combinedClickable(
                interactionSource = source,
                indication = null,
                onClick = onToggle,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(lerpColor(palette.bg, pink, 0.28f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = YbowIcons.EyeOff,
                contentDescription = null,
                tint = pink,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Gizli mod",
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                color = palette.text,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (incognito) {
                    "Açık. Bu sekmede geçmiş kaydedilmiyor."
                } else {
                    "Bu sekmede geçmiş kaydedilmez."
                },
                style = TextStyle(fontSize = 13.sp),
                color = palette.muted,
            )
        }
        YbowSwitch(palette = palette, checked = incognito)
    }
}

@Composable
private fun YbowSwitch(palette: com.ybow.browser.ui.theme.YbowPalette, checked: Boolean) {
    val knob by animateDpAsState(
        targetValue = if (checked) 18.dp else 0.dp,
        animationSpec = tween(YbowMotion.DurMillis, easing = YbowMotion.Spring),
        label = "switchKnob",
    )
    val track by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(YbowMotion.FastMillis),
        label = "switchTrack",
    )
    Box(
        modifier = Modifier
            .width(46.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(lerpColor(palette.bg, palette.accent, 0.30f * track))
            .border(1.dp, if (checked) palette.accent else palette.line, CircleShape)
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = knob)
                .size(20.dp)
                .clip(CircleShape)
                .background(lerpColor(palette.muted, palette.accent, track)),
        )
    }
}

// ---------------------------------------------------------------- small helpers

private fun Modifier.graphicsLayerAlphaPop(pop: Float): Modifier =
    graphicsLayer {
        if (pop > 0f) {
            val s = 0.3f + 0.7f * pop
            scaleX = s
            scaleY = s
            alpha = pop
        }
    }

private fun Modifier.dashedBorder(color: Color, radius: androidx.compose.ui.unit.Dp): Modifier =
    drawBehind {
        val stroke = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f)),
        )
        drawRoundRect(
            color = color,
            topLeft = Offset.Zero,
            size = Size(size.width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius.toPx()),
            style = stroke,
        )
    }

private fun lerpColor(from: Color, to: Color, fraction: Float): Color =
    androidx.compose.ui.graphics.lerp(from, to, fraction.coerceIn(0f, 1f))
