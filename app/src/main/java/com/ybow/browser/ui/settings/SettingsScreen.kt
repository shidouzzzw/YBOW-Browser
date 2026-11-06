package com.ybow.browser.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.BuildConfig
import com.ybow.browser.R
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.data.SearchEngine
import com.ybow.browser.data.ThemeMode
import com.ybow.browser.ui.components.IconChip
import com.ybow.browser.ui.components.YbowCard
import com.ybow.browser.ui.components.YbowTopBar

@Composable
fun SettingsScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var homepageDialog by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to colors.primaryContainer.copy(alpha = 0.55f),
                    0.38f to colors.background,
                    1f to colors.background,
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            YbowTopBar(
                title = stringResource(R.string.settings_title),
                subtitle = stringResource(R.string.app_tagline),
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp),
            ) {
                SectionHeader(stringResource(R.string.settings_section_search))

                YbowCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconChip(icon = Icons.Rounded.Search)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_search_engine),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = stringResource(R.string.settings_search_engine_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SearchEngine.entries.forEach { engine ->
                            OptionPill(
                                label = engine.label,
                                selected = settings.searchEngine == engine,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setSearchEngine(engine) },
                            )
                        }
                    }
                }

                SectionHeader(stringResource(R.string.settings_section_appearance))

                YbowCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconChip(icon = Icons.Rounded.Palette)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_theme),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = stringResource(R.string.settings_theme_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ThemeChoice(
                            label = stringResource(R.string.settings_theme_system),
                            mode = ThemeMode.SYSTEM,
                            selected = settings.themeMode,
                            modifier = Modifier.weight(1f),
                        ) { viewModel.setThemeMode(it) }
                        ThemeChoice(
                            label = stringResource(R.string.settings_theme_light),
                            mode = ThemeMode.LIGHT,
                            selected = settings.themeMode,
                            modifier = Modifier.weight(1f),
                        ) { viewModel.setThemeMode(it) }
                        ThemeChoice(
                            label = stringResource(R.string.settings_theme_dark),
                            mode = ThemeMode.DARK,
                            selected = settings.themeMode,
                            modifier = Modifier.weight(1f),
                        ) { viewModel.setThemeMode(it) }
                    }
                    RowDivider()
                    ActionRow(
                        icon = Icons.Rounded.Home,
                        title = stringResource(R.string.settings_homepage),
                        subtitle = settings.homepage.ifBlank {
                            stringResource(R.string.settings_homepage_desc)
                        },
                        onClick = { homepageDialog = true },
                    )
                }

                SectionHeader(stringResource(R.string.settings_section_privacy))

                YbowCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ToggleRow(
                        icon = Icons.Rounded.Code,
                        title = stringResource(R.string.settings_javascript),
                        subtitle = stringResource(R.string.settings_javascript_desc),
                        checked = settings.javaScriptEnabled,
                        onCheckedChange = { viewModel.setJavaScriptEnabled(it) },
                    )
                    RowDivider()
                    ToggleRow(
                        icon = Icons.Rounded.Shield,
                        title = stringResource(R.string.settings_third_party_cookies),
                        subtitle = stringResource(R.string.settings_third_party_cookies_desc),
                        checked = settings.thirdPartyCookiesEnabled,
                        onCheckedChange = { viewModel.setThirdPartyCookiesEnabled(it) },
                    )
                    RowDivider()
                    ToggleRow(
                        icon = Icons.Rounded.History,
                        title = stringResource(R.string.settings_save_history),
                        subtitle = stringResource(R.string.settings_save_history_desc),
                        checked = settings.saveHistory,
                        onCheckedChange = { viewModel.setSaveHistory(it) },
                    )
                    RowDivider()
                    InfoRow(
                        icon = Icons.Rounded.Lock,
                        title = stringResource(R.string.settings_safe_browsing),
                        subtitle = stringResource(R.string.settings_safe_browsing_desc),
                    )
                }

                SectionHeader(stringResource(R.string.settings_section_data))

                YbowCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ActionRow(
                        icon = Icons.Rounded.DeleteSweep,
                        title = stringResource(R.string.settings_clear_cache),
                        subtitle = stringResource(R.string.settings_clear_cache_desc),
                        onClick = { viewModel.clearCache() },
                    )
                    RowDivider()
                    ActionRow(
                        icon = Icons.Rounded.DeleteOutline,
                        title = stringResource(R.string.settings_clear_cookies),
                        subtitle = stringResource(R.string.settings_clear_cookies_desc),
                        onClick = { viewModel.clearCookies() },
                    )
                    RowDivider()
                    ActionRow(
                        icon = Icons.Rounded.History,
                        title = stringResource(R.string.settings_clear_history),
                        subtitle = stringResource(R.string.settings_clear_history_desc),
                        onClick = { viewModel.clearHistory() },
                    )
                }

                SectionHeader(stringResource(R.string.settings_about))

                YbowCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(16.dp))
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
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "YBOW",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    letterSpacing = 2.sp,
                                ),
                            )
                            Text(
                                text = stringResource(
                                    R.string.settings_version,
                                    BuildConfig.VERSION_NAME,
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.onSurfaceVariant,
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    RowDivider()
                    Text(
                        text = stringResource(R.string.settings_about_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }

    if (homepageDialog) {
        var draft by remember { mutableStateOf(settings.homepage) }
        AlertDialog(
            onDismissRequest = { homepageDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text(stringResource(R.string.settings_homepage)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.settings_homepage_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text(stringResource(R.string.settings_homepage_hint)) },
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setHomepage(draft)
                    homepageDialog = false
                }) {
                    Text(stringResource(R.string.action_go))
                }
            },
            dismissButton = {
                TextButton(onClick = { homepageDialog = false }) {
                    Text(stringResource(R.string.action_close))
                }
            },
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.6.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 24.dp, top = 22.dp, bottom = 9.dp),
    )
}

@Composable
private fun OptionPill(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) colors.primaryContainer else colors.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(
            1.dp,
            if (selected) colors.primary.copy(alpha = 0.45f) else colors.outlineVariant.copy(alpha = 0.5f),
        ),
        modifier = modifier.height(44.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(Modifier.width(5.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ThemeChoice(
    label: String,
    mode: ThemeMode,
    selected: ThemeMode,
    modifier: Modifier = Modifier,
    onSelect: (ThemeMode) -> Unit,
) {
    OptionPill(
        label = label,
        selected = selected == mode,
        modifier = modifier,
        onClick = { onSelect(mode) },
    )
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
        modifier = Modifier.padding(start = 66.dp, end = 14.dp),
    )
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconChip(icon = icon)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimary,
                checkedTrackColor = colors.primary,
                checkedBorderColor = colors.primary,
                uncheckedThumbColor = colors.outline,
                uncheckedTrackColor = colors.surfaceVariant,
                uncheckedBorderColor = colors.outline.copy(alpha = 0.6f),
            ),
        )
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconChip(icon = icon)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.Top,
    ) {
        IconChip(icon = icon)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
