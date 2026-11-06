package com.ybow.browser.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ybow.browser.R
import com.ybow.browser.data.local.Shortcut
import com.ybow.browser.ui.browser.BottomBarContentPadding
import com.ybow.browser.util.FaviconStore
import com.ybow.browser.util.UrlTools

object HomeScreenKeys

@Composable
fun HomeScreen(
    searchEngineLabel: String,
    shortcuts: List<Shortcut>,
    onOpenSearch: () -> Unit,
    onOpenShortcut: (Shortcut) -> Unit,
    onAddShortcut: (url: String, name: String) -> Unit,
    onUpdateShortcut: (Shortcut, url: String, name: String) -> Unit,
    onDeleteShortcut: (Shortcut) -> Unit,
    onShowBookmarks: () -> Unit,
    onIncognito: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var editorOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Shortcut?>(null) }
    var actionTarget by remember { mutableStateOf<Shortcut?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        SeaWaves()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(21.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(colors.primary, colors.secondary),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Waves,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "YBOW",
                style = MaterialTheme.typography.displayLarge.copy(
                    letterSpacing = 9.sp,
                    fontWeight = FontWeight.Black,
                ),
                color = colors.onBackground,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )

            Spacer(Modifier.height(28.dp))

            Surface(
                onClick = onOpenSearch,
                shape = RoundedCornerShape(28.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.8f)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.address_bar_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Surface(
                        onClick = onOpenSearch,
                        shape = CircleShape,
                        color = colors.primary,
                        modifier = Modifier.size(42.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = stringResource(R.string.action_go),
                                tint = colors.onPrimary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            Text(
                text = stringResource(R.string.home_search_engine, searchEngineLabel),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, end = 4.dp),
                textAlign = TextAlign.End,
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.home_quick_access),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    onClick = {
                        editing = null
                        editorOpen = true
                    },
                    shape = CircleShape,
                    color = colors.primary.copy(alpha = 0.16f),
                    modifier = Modifier.size(28.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.shortcut_add),
                            tint = colors.primary,
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onShowBookmarks) {
                    Text(
                        text = stringResource(R.string.bookmarks_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.primary,
                    )
                }
            }

            if (shortcuts.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = colors.surface.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkBorder,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.shortcut_empty),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                shortcuts.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowItems.forEach { shortcut ->
                            ShortcutTile(
                                shortcut = shortcut,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenShortcut(shortcut) },
                                onLongClick = { actionTarget = shortcut },
                            )
                        }
                        repeat(4 - rowItems.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Surface(
                onClick = onIncognito,
                shape = MaterialTheme.shapes.extraLarge,
                color = colors.surface,
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.background(
                        Brush.linearGradient(
                            listOf(
                                colors.secondaryContainer.copy(alpha = 0.55f),
                                colors.surface.copy(alpha = 0.2f),
                            ),
                        ),
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.secondary.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VisibilityOff,
                                contentDescription = null,
                                tint = colors.secondary,
                                modifier = Modifier.size(21.dp),
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.home_incognito),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onSurface,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.home_incognito_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(BottomBarContentPadding))
        }
    }

    if (editorOpen) {
        ShortcutEditorDialog(
            initial = editing,
            onDismiss = {
                editorOpen = false
                editing = null
            },
            onConfirm = { url, name ->
                val target = editing
                if (target == null) onAddShortcut(url, name) else onUpdateShortcut(target, url, name)
                editorOpen = false
                editing = null
            },
        )
    }

    actionTarget?.let { shortcut ->
        AlertDialog(
            onDismissRequest = { actionTarget = null },
            title = { Text(shortcut.title) },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            actionTarget = null
                            editing = shortcut
                            editorOpen = true
                        },
                    ) {
                        Text(stringResource(R.string.shortcut_edit), color = colors.primary)
                    }
                    TextButton(
                        onClick = {
                            actionTarget = null
                            onDeleteShortcut(shortcut)
                        },
                    ) {
                        Text(stringResource(R.string.shortcut_delete), color = colors.primary)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { actionTarget = null }) {
                    Text(stringResource(R.string.shortcut_cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShortcutTile(
    shortcut: Shortcut,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShortcutIcon(url = shortcut.url, size = 46.dp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = shortcut.title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

/** Round favicon for a shortcut, falling back to the site's first letter. */
@Composable
private fun ShortcutIcon(url: String, size: Dp) {
    val colors = MaterialTheme.colorScheme
    var icon by remember(url) { mutableStateOf(FaviconStore.cached(url)) }

    LaunchedEffect(url) {
        if (icon == null) icon = FaviconStore.load(url)
    }

    val bitmap: ImageBitmap? = icon
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(colors.surface),
        )
    } else {
        val letter = UrlTools.hostOf(url)
            .firstOrNull { it.isLetterOrDigit() }
            ?.uppercaseChar()
            ?.toString()
            .orEmpty()
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = letter,
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary,
            )
        }
    }
}

@Composable
private fun ShortcutEditorDialog(
    initial: Shortcut?,
    onDismiss: () -> Unit,
    onConfirm: (url: String, name: String) -> Unit,
) {
    var address by remember { mutableStateOf(initial?.url.orEmpty()) }
    var name by remember { mutableStateOf(initial?.title.orEmpty()) }
    val colors = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (initial == null) R.string.shortcut_add_title else R.string.shortcut_edit_title,
                ),
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.shortcut_url_label)) },
                    placeholder = { Text(stringResource(R.string.shortcut_url_hint)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.shortcut_name_label)) },
                    placeholder = { Text(stringResource(R.string.shortcut_name_hint)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(address, name) },
                enabled = address.isNotBlank(),
            ) {
                Text(stringResource(R.string.shortcut_save), color = colors.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.shortcut_cancel))
            }
        },
    )
}

/** Two quiet wave bands anchored to the bottom of the home content. */
@Composable
private fun SeaWaves() {
    val colors = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.BottomCenter),
        ) {
            val width = size.width
            val height = size.height
            val step = width / 2f
            for (index in 0..1) {
                val baseY = height * (0.66f + index * 0.16f)
                val amplitude = 22f + index * 14f
                val path = Path().apply {
                    moveTo(0f, baseY)
                    var x = 0f
                    while (x < width) {
                        quadraticBezierTo(
                            x + step / 2f,
                            baseY - amplitude,
                            x + step,
                            baseY,
                        )
                        x += step
                    }
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = path,
                    color = colors.secondary.copy(alpha = 0.10f - index * 0.04f),
                )
            }
        }
    }
}
