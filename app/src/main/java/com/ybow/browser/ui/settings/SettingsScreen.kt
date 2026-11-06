package com.ybow.browser.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybow.browser.BuildConfig
import com.ybow.browser.R
import com.ybow.browser.browser.BrowserViewModel
import com.ybow.browser.data.SearchEngine
import com.ybow.browser.data.ThemeMode
import com.ybow.browser.ui.components.SectionLabel
import com.ybow.browser.ui.components.YbowTopBar

@Composable
fun SettingsScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var homepageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        YbowTopBar(
            title = stringResource(R.string.settings_title),
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(bottom = 28.dp),
        ) {
            SectionLabel(stringResource(R.string.settings_section_search))
            SettingDescription(stringResource(R.string.settings_search_engine_desc))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SearchEngine.entries.forEach { engine ->
                    FilterChip(
                        selected = settings.searchEngine == engine,
                        onClick = { viewModel.setSearchEngine(engine) },
                        label = { Text(engine.label) },
                    )
                }
            }

            SectionLabel(stringResource(R.string.settings_section_appearance))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ThemeChoice(stringResource(R.string.settings_theme_system), ThemeMode.SYSTEM, settings.themeMode) {
                    viewModel.setThemeMode(it)
                }
                ThemeChoice(stringResource(R.string.settings_theme_light), ThemeMode.LIGHT, settings.themeMode) {
                    viewModel.setThemeMode(it)
                }
                ThemeChoice(stringResource(R.string.settings_theme_dark), ThemeMode.DARK, settings.themeMode) {
                    viewModel.setThemeMode(it)
                }
            }

            ClickableRow(
                title = stringResource(R.string.settings_homepage),
                subtitle = settings.homepage.ifBlank {
                    stringResource(R.string.settings_homepage_desc)
                },
                onClick = { homepageDialog = true },
            )

            SectionLabel(stringResource(R.string.settings_section_privacy))

            SwitchRow(
                title = stringResource(R.string.settings_javascript),
                subtitle = stringResource(R.string.settings_javascript_desc),
                checked = settings.javaScriptEnabled,
                onCheckedChange = { viewModel.setJavaScriptEnabled(it) },
            )
            SwitchRow(
                title = stringResource(R.string.settings_third_party_cookies),
                subtitle = stringResource(R.string.settings_third_party_cookies_desc),
                checked = settings.thirdPartyCookiesEnabled,
                onCheckedChange = { viewModel.setThirdPartyCookiesEnabled(it) },
            )
            SwitchRow(
                title = stringResource(R.string.settings_save_history),
                subtitle = stringResource(R.string.settings_save_history_desc),
                checked = settings.saveHistory,
                onCheckedChange = { viewModel.setSaveHistory(it) },
            )

            InfoRow(
                title = stringResource(R.string.settings_safe_browsing),
                subtitle = stringResource(R.string.settings_safe_browsing_desc),
                icon = Icons.Rounded.Shield,
            )

            SectionLabel(stringResource(R.string.settings_section_data))
            ClickableRow(
                title = stringResource(R.string.settings_clear_cache),
                subtitle = null,
                icon = Icons.Rounded.DeleteSweep,
                onClick = { viewModel.clearCache() },
            )
            ClickableRow(
                title = stringResource(R.string.settings_clear_cookies),
                subtitle = null,
                icon = Icons.Rounded.Lock,
                onClick = { viewModel.clearCookies() },
            )
            ClickableRow(
                title = stringResource(R.string.settings_clear_history),
                subtitle = null,
                icon = Icons.Rounded.History,
                onClick = { viewModel.clearHistory() },
            )

            SectionLabel(stringResource(R.string.settings_about))
            InfoRow(
                title = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
                subtitle = stringResource(R.string.settings_about_desc),
                icon = Icons.Rounded.Info,
            )
        }
    }

    if (homepageDialog) {
        var draft by remember { mutableStateOf(settings.homepage) }
        AlertDialog(
            onDismissRequest = { homepageDialog = false },
            title = { Text(stringResource(R.string.settings_homepage)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.settings_homepage_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        singleLine = true,
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
private fun ThemeChoice(
    label: String,
    mode: ThemeMode,
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    FilterChip(
        selected = selected == mode,
        onClick = { onSelect(mode) },
        label = { Text(label) },
    )
}

@Composable
private fun SettingDescription(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
    )
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(start = 16.dp),
    )
}

@Composable
private fun ClickableRow(
    title: String,
    subtitle: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 14.dp),
                )
            }
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
            )
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(start = 16.dp),
    )
}

@Composable
private fun InfoRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 14.dp, top = 2.dp),
        )
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
