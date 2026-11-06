package com.ybow.browser.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ybow.browser.R
import com.ybow.browser.data.local.Bookmark
import com.ybow.browser.data.local.HistoryEntry
import com.ybow.browser.util.UrlTools

private data class SearchSuggestion(
    val title: String,
    val subtitle: String,
    val value: String,
    val fromBookmark: Boolean,
)

/**
 * Full screen search surface: typing shows local suggestions (bookmarks first, then
 * history), the first row always runs the query, and submitting loads the results
 * page. Nothing is sent anywhere except the chosen search engine when you submit.
 */
@Composable
fun SearchScreen(
    history: List<HistoryEntry>,
    bookmarks: List<Bookmark>,
    searchEngineLabel: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun submit(raw: String) {
        val text = raw.trim()
        if (text.isEmpty()) return
        keyboard?.hide()
        focusManager.clearFocus()
        onSubmit(text)
    }

    val suggestions = remember(query, history, bookmarks) {
        val needle = query.trim().lowercase()
        if (needle.isEmpty()) {
            emptyList()
        } else {
            val fromBookmarks = bookmarks
                .filter { it.title.lowercase().contains(needle) || it.url.lowercase().contains(needle) }
                .map {
                    SearchSuggestion(
                        title = it.title,
                        subtitle = UrlTools.displayUrl(it.url),
                        value = it.url,
                        fromBookmark = true,
                    )
                }
            val fromHistory = history
                .filter { it.title.lowercase().contains(needle) || it.url.lowercase().contains(needle) }
                .map {
                    SearchSuggestion(
                        title = it.title,
                        subtitle = UrlTools.displayUrl(it.url),
                        value = it.url,
                        fromBookmark = false,
                    )
                }
            (fromBookmarks + fromHistory)
                .distinctBy { it.value }
                .take(8)
        }
    }

    val recent = remember(history) {
        history
            .distinctBy { UrlTools.displayUrl(it.url) }
            .take(8)
            .map {
                SearchSuggestion(
                    title = it.title.ifBlank { UrlTools.displayUrl(it.url) },
                    subtitle = UrlTools.displayUrl(it.url),
                    value = it.url,
                    fromBookmark = false,
                )
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .imePadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    onClick = onDismiss,
                    shape = CircleShape,
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.8f)),
                    modifier = Modifier.size(42.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.action_close),
                            tint = colors.onSurface,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.45f)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(start = 14.dp, end = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(19.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = colors.onSurface,
                                fontSize = 15.5.sp,
                            ),
                            cursorBrush = SolidColor(colors.primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { submit(query) }),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (query.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.address_bar_hint),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = colors.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                        )
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = { query = "" },
                                modifier = Modifier.size(34.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.action_clear),
                                    tint = colors.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
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
                    .padding(end = 22.dp, top = 2.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            ) {
                if (query.isNotBlank()) {
                    item(key = "query") {
                        SuggestionRow(
                            icon = Icons.Rounded.Search,
                            title = stringResource(R.string.search_action, query.trim()),
                            subtitle = stringResource(R.string.search_run_hint),
                            highlighted = true,
                            onClick = { submit(query) },
                            onFill = null,
                        )
                    }
                }

                if (suggestions.isNotEmpty()) {
                    item(key = "suggestions-label") {
                        SectionHeader(stringResource(R.string.search_suggestions))
                    }
                    items(suggestions, key = { "s-" + it.value }) { suggestion ->
                        SuggestionRow(
                            icon = if (suggestion.fromBookmark) Icons.Rounded.StarBorder else Icons.Rounded.History,
                            title = suggestion.title,
                            subtitle = suggestion.subtitle,
                            highlighted = false,
                            onClick = { onSubmitFromRow(suggestion, ::submit) },
                            onFill = { query = suggestion.title },
                            onOpenNewTab = { onOpenUrl(suggestion.value) },
                        )
                    }
                } else if (query.isNotBlank()) {
                    item(key = "no-suggestions") {
                        Text(
                            text = stringResource(R.string.search_no_suggestions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                        )
                    }
                }

                if (query.isBlank() && recent.isNotEmpty()) {
                    item(key = "recent-label") {
                        SectionHeader(stringResource(R.string.search_recent))
                    }
                    items(recent, key = { "r-" + it.value }) { suggestion ->
                        SuggestionRow(
                            icon = Icons.Rounded.History,
                            title = suggestion.title,
                            subtitle = suggestion.subtitle,
                            highlighted = false,
                            onClick = { onSubmitFromRow(suggestion, ::submit) },
                            onFill = { query = suggestion.title },
                            onOpenNewTab = { onOpenUrl(suggestion.value) },
                        )
                    }
                }
            }
        }
    }
}

private fun onSubmitFromRow(suggestion: SearchSuggestion, submit: (String) -> Unit) {
    // Rows that point to a real page open it directly instead of searching for it.
    submit(if (suggestion.value.startsWith("http")) suggestion.value else suggestion.title)
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.4.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 22.dp, top = 14.dp, bottom = 6.dp),
    )
}

@Composable
private fun SuggestionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    highlighted: Boolean,
    onClick: () -> Unit,
    onFill: (() -> Unit)?,
    onOpenNewTab: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (highlighted) colors.primaryContainer.copy(alpha = 0.55f) else colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(colors.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (onFill != null) {
                IconButton(onClick = onFill, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.NorthEast,
                        contentDescription = stringResource(R.string.search_fill_hint),
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(17.dp),
                    )
                }
            }
        }
    }
}
