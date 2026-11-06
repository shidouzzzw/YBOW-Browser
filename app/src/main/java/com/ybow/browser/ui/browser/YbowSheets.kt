@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.ybow.browser.ui.browser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ybow.browser.data.local.Shortcut
import com.ybow.browser.ui.theme.LocalYbowPalette
import com.ybow.browser.ui.theme.YbowIcons
import com.ybow.browser.ui.theme.YbowMotion
import com.ybow.browser.ui.theme.YbowPalette
import com.ybow.browser.ui.theme.pressScale
import com.ybow.browser.ui.theme.siteIconBackground
import com.ybow.browser.ui.theme.siteIconForeground
import com.ybow.browser.util.UrlTools
import kotlinx.coroutines.delay

/** Which sheet, if any, is currently open above the interface. */
sealed interface BrowserSheet {
    data object None : BrowserSheet
    data object Menu : BrowserSheet
    data class Edit(val shortcut: Shortcut?) : BrowserSheet
    data class Item(val shortcut: Shortcut) : BrowserSheet
    data class Search(val initialUrl: String) : BrowserSheet
}

@Composable
fun SheetScrim(visible: Boolean, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(160)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.62f))
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )
    }
}

/** The rounded bottom panel shared by every sheet in the design. */
@Composable
fun SheetSurface(
    content: @Composable () -> Unit,
) {
    val palette = LocalYbowPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(palette.card, palette.cardRaised),
                ),
            )
            .border(1.dp, palette.line, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp, bottom = 26.dp),
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(palette.text.copy(alpha = 0.22f))
                .align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
}

/**
 * Omnibox sheet: lets the user enter a URL or search query without leaving their
 * current webpage until they actually submit.
 */
@Composable
fun OmniboxSheet(
    initialUrl: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    val palette = LocalYbowPalette.current
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf(if (UrlTools.isHttpUrl(initialUrl)) initialUrl else "") }

    LaunchedEffect(Unit) {
        delay(260)
        runCatching { focusRequester.requestFocus() }
    }

    SheetSurface {
        Text(
            text = "Ara veya adres yaz",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = palette.text,
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.bg)
                    .border(1.2.dp, palette.accent, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = YbowIcons.Search,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 16.sp, color = palette.text),
                        cursorBrush = SolidColor(palette.accent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            val t = text.trim()
                            if (t.isNotEmpty()) onSubmit(t)
                        }),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        decorationBox = { inner ->
                            if (text.isEmpty()) {
                                Text(
                                    text = "ornek.com veya kelime...",
                                    style = TextStyle(fontSize = 15.sp),
                                    color = palette.muted,
                                )
                            }
                            inner()
                        },
                    )
                    if (text.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { text = "" },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = YbowIcons.Close,
                                contentDescription = "Temizle",
                                tint = palette.muted,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            val goSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .pressScale(goSource, YbowMotion.PressGo, highlight = true)
                    .clip(CircleShape)
                    .background(palette.accent)
                    .combinedClickable(
                        interactionSource = goSource,
                        indication = null,
                        onClick = {
                            val t = text.trim()
                            if (t.isNotEmpty()) onSubmit(t)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = YbowIcons.Arrow,
                    contentDescription = "Git",
                    tint = palette.bg,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
fun ShortcutSheet(
    shortcut: Shortcut?,
    onDismiss: () -> Unit,
    onSave: (host: String, name: String) -> Unit,
) {
    val palette = LocalYbowPalette.current
    val focusManager = LocalFocusManager.current
    val urlFocus = remember { FocusRequester() }
    var address by remember(shortcut) { mutableStateOf(shortcut?.let { UrlTools.hostOf(it.url) }.orEmpty()) }
    var name by remember(shortcut) { mutableStateOf(shortcut?.title.orEmpty()) }
    var error by remember { mutableStateOf("") }
    var shakes by remember { mutableIntStateOf(0) }
    val shift = remember { Animatable(0f) }

    LaunchedEffect(shakes) {
        if (shakes == 0) return@LaunchedEffect
        for (value in listOf(-6f, 6f, -6f, 6f, 0f)) {
            shift.snapTo(value)
            delay(76)
        }
    }

    LaunchedEffect(Unit) {
        delay(380)
        runCatching { urlFocus.requestFocus() }
    }

    val host = parseHost(address)
    val previewName = name.trim().ifBlank { host.takeIf { it.isNotBlank() }?.let { nameFromHost(it) } ?: "" }

    SheetSurface {
        Text(
            text = if (shortcut == null) "Kısayol ekle" else "Kısayolu düzenle",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = palette.text,
        )
        Spacer(Modifier.height(16.dp))

        Text("Site adresi", style = TextStyle(fontSize = 13.sp), color = palette.muted)
        Spacer(Modifier.height(6.dp))
        Box(modifier = Modifier.offset { IntOffset(shift.value.dp.roundToPx(), 0) }) {
            SheetField(
                value = address,
                onValueChange = { address = it },
                placeholder = "ornek.com",
                focusRequester = urlFocus,
                imeAction = ImeAction.Next,
            )
        }
        Spacer(Modifier.height(14.dp))

        Text("Ad (isteğe bağlı)", style = TextStyle(fontSize = 13.sp), color = palette.muted)
        Spacer(Modifier.height(6.dp))
        SheetField(
            value = name,
            onValueChange = { if (it.length <= 14) name = it },
            placeholder = "Ornek",
            focusRequester = null,
            imeAction = ImeAction.Done,
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (host.isBlank()) palette.bg else siteIconBackground(host),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (host.isBlank()) "?" else previewName.take(1).uppercase(),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = if (host.isBlank()) palette.muted else siteIconForeground(host),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (host.isBlank()) {
                    "Önizleme burada görünür"
                } else {
                    "$previewName · $host"
                },
                style = TextStyle(fontSize = 13.5.sp),
                color = palette.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = error,
            style = TextStyle(fontSize = 13.sp),
            color = palette.error,
            modifier = Modifier.height(18.dp),
        )
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SheetButton(
                label = "Vazgeç",
                primary = false,
                modifier = Modifier.weight(1f),
                onClick = onDismiss,
            )
            SheetButton(
                label = if (shortcut == null) "Ekle" else "Kaydet",
                primary = true,
                modifier = Modifier.weight(1f),
                onClick = {
                    focusManager.clearFocus()
                    val parsed = parseHost(address)
                    if (parsed.isBlank()) {
                        error = "Geçerli bir adres yaz, örneğin ornek.com"
                        shakes++
                        return@SheetButton
                    }
                    onSave(parsed, name.trim().ifBlank { nameFromHost(parsed) })
                },
            )
        }
    }
}

@Composable
fun ShortcutOptionsSheet(
    shortcut: Shortcut,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val palette = LocalYbowPalette.current
    val host = UrlTools.hostOf(shortcut.url)

    SheetSurface {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(siteIconBackground(host)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = shortcut.title.take(1).uppercase(),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = siteIconForeground(host),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = shortcut.title,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    color = palette.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    host,
                    style = TextStyle(fontSize = 13.5.sp),
                    color = palette.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SheetButton(
                label = "Aç",
                icon = YbowIcons.External,
                primary = false,
                onClick = onOpen,
            )
            SheetButton(
                label = "Düzenle",
                icon = YbowIcons.Pencil,
                primary = false,
                onClick = onEdit,
            )
            SheetButton(
                label = "Sil",
                icon = YbowIcons.Trash,
                primary = false,
                danger = true,
                onClick = onDelete,
            )
        }
    }
}

@Composable
fun MenuSheet(
    hasPage: Boolean,
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    onNewTab: () -> Unit,
    onIncognitoTab: () -> Unit,
    onBookmarks: () -> Unit,
    onHistory: () -> Unit,
    onDownloads: () -> Unit,
    onSettings: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit,
) {
    val palette = LocalYbowPalette.current
    val items = listOf(
        Triple("Yeni sekme", YbowIcons.Plus, onNewTab),
        Triple("Gizli sekme", YbowIcons.EyeOff, onIncognitoTab),
        Triple("Yer imleri", YbowIcons.Bookmark, onBookmarks),
        Triple("Geçmiş", YbowIcons.Clock, onHistory),
        Triple("İndirilenler", YbowIcons.Download, onDownloads),
        Triple("Ayarlar", YbowIcons.Sliders, onSettings),
    )

    SheetSurface {
        if (hasPage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PageActionButton(
                    icon = YbowIcons.Bookmark,
                    label = if (isBookmarked) "Yer iminden çıkar" else "Yer imlerine ekle",
                    tint = if (isBookmarked) palette.accent else palette.text,
                    modifier = Modifier.weight(1f),
                    onClick = onToggleBookmark,
                )
                PageActionButton(
                    icon = YbowIcons.Share,
                    label = "Paylaş",
                    tint = palette.text,
                    modifier = Modifier.weight(1f),
                    onClick = onShare,
                )
                PageActionButton(
                    icon = YbowIcons.Copy,
                    label = "Kopyala",
                    tint = palette.text,
                    modifier = Modifier.weight(1f),
                    onClick = onCopy,
                )
            }
        }

        items.chunked(3).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { (label, icon, action) ->
                    val source = remember { MutableInteractionSource() }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .pressScale(source, YbowMotion.PressMenu, highlight = true)
                            .clip(RoundedCornerShape(18.dp))
                            .background(palette.cardRaised)
                            .border(1.dp, palette.line, RoundedCornerShape(18.dp))
                            .combinedClickable(
                                interactionSource = source,
                                indication = null,
                                onClick = action,
                            )
                            .padding(vertical = 15.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = palette.accent,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = label,
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            color = palette.text,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalYbowPalette.current
    val source = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .height(48.dp)
            .pressScale(source, YbowMotion.PressButton, highlight = true)
            .clip(RoundedCornerShape(14.dp))
            .background(palette.cardRaised)
            .border(1.dp, palette.line, RoundedCornerShape(14.dp))
            .combinedClickable(
                interactionSource = source,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(17.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Medium),
            color = palette.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ToastBanner(text: String?, modifier: Modifier = Modifier) {
    val palette = LocalYbowPalette.current
    AnimatedVisibility(
        visible = text != null,
        enter = fadeIn(tween(160)) + androidx.compose.animation.slideInVertically(
            animationSpec = tween(YbowMotion.FastMillis),
            initialOffsetY = { it / 3 },
        ),
        exit = fadeOut(tween(140)),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clip(CircleShape)
                .background(palette.text)
                .padding(horizontal = 18.dp, vertical = 11.dp),
        ) {
            Text(
                text = text.orEmpty(),
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                color = palette.bg,
            )
        }
    }
}

@Composable
private fun SheetField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    focusRequester: FocusRequester?,
    imeAction: ImeAction,
) {
    val palette = LocalYbowPalette.current
    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp, color = palette.text),
        cursorBrush = SolidColor(palette.accent),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.bg)
            .border(
                width = 1.dp,
                color = if (focused) palette.accent else palette.line,
                shape = RoundedCornerShape(16.dp),
            )
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChangedCompat { focused = it }
            .padding(horizontal = 16.dp),
        decorationBox = { inner ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = 16.sp),
                        color = palette.muted,
                    )
                }
                inner()
            }
        },
    )
}

private fun Modifier.onFocusChangedCompat(onChanged: (Boolean) -> Unit): Modifier =
    onFocusChanged { onChanged(it.isFocused) }

@Composable
private fun SheetButton(
    label: String,
    primary: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    danger: Boolean = false,
    onClick: () -> Unit,
) {
    val palette = LocalYbowPalette.current
    val source = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .pressScale(source, YbowMotion.PressButton, highlight = true)
            .clip(RoundedCornerShape(16.dp))
            .background(if (primary) palette.accent else palette.bg)
            .border(1.dp, if (primary) Color.Transparent else palette.line, RoundedCornerShape(16.dp))
            .combinedClickable(
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (primary) palette.bg else if (danger) palette.error else palette.text,
                modifier = Modifier.size(19.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = label,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = if (primary) palette.bg else if (danger) palette.error else palette.text,
        )
    }
}

/** Host parser: strips scheme, www and any path or query. */
fun parseHost(raw: String): String {
    var value = raw.trim().lowercase()
    value = value.removePrefix("https://").removePrefix("http://").removePrefix("www.")
    val cut = value.indexOfFirst { it == '/' || it == '?' || it == '#' }
    if (cut >= 0) value = value.substring(0, cut)
    val pattern = Regex("^[a-z0-9-]+(\\.[a-z0-9-]+)+$")
    return if (pattern.matches(value)) value else ""
}

fun nameFromHost(host: String): String {
    val first = host.substringBefore('.').ifBlank { host }
    return first.replaceFirstChar { it.uppercaseChar() }
}
