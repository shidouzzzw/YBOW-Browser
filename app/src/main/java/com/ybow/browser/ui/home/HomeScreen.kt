package com.ybow.browser.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ybow.browser.R
import com.ybow.browser.data.local.Bookmark
import com.ybow.browser.ui.components.FaviconBadge

object HomeScreenKeys

@Composable
fun HomeScreen(
    searchEngineLabel: String,
    bookmarks: List<Bookmark>,
    onOpenSearch: () -> Unit,
    onOpenBookmark: (Bookmark) -> Unit,
    onShowBookmarks: () -> Unit,
    onIncognito: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

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
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onShowBookmarks) {
                    Text(
                        text = stringResource(R.string.bookmarks_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.primary,
                    )
                }
            }

            if (bookmarks.isEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = colors.surface.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkBorder,
                            contentDescription = null,
                            tint = colors.primary,
                        )
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = stringResource(R.string.home_no_bookmarks),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                bookmarks.take(8).chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        rowItems.forEach { bookmark ->
                            QuickAccessTile(
                                bookmark = bookmark,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenBookmark(bookmark) },
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

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun QuickAccessTile(
    bookmark: Bookmark,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FaviconBadge(url = bookmark.url, size = 36.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = bookmark.title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
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
