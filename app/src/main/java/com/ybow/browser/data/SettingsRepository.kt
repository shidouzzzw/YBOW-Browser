package com.ybow.browser.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import java.net.URLEncoder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

enum class SearchEngine(val label: String, private val queryUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q=%s"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=%s");

    fun search(query: String): String =
        queryUrl.replace("%s", URLEncoder.encode(query, "UTF-8"))

    companion object {
        fun fromName(name: String?): SearchEngine =
            entries.firstOrNull { it.name == name } ?: GOOGLE
    }
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromName(name: String?): ThemeMode =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}

data class AppSettings(
    val searchEngine: SearchEngine = SearchEngine.GOOGLE,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val homepage: String = "",
    val javaScriptEnabled: Boolean = true,
    val thirdPartyCookiesEnabled: Boolean = false,
    val saveHistory: Boolean = true,
)

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "ybow_settings")

/**
 * Local-only settings storage. Nothing here ever leaves the device.
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HOMEPAGE = stringPreferencesKey("homepage")
        val JAVASCRIPT = booleanPreferencesKey("javascript_enabled")
        val THIRD_PARTY_COOKIES = booleanPreferencesKey("third_party_cookies")
        val SAVE_HISTORY = booleanPreferencesKey("save_history")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            AppSettings(
                searchEngine = SearchEngine.fromName(prefs[Keys.SEARCH_ENGINE]),
                themeMode = ThemeMode.fromName(prefs[Keys.THEME_MODE]),
                homepage = prefs[Keys.HOMEPAGE].orEmpty(),
                javaScriptEnabled = prefs[Keys.JAVASCRIPT] ?: true,
                thirdPartyCookiesEnabled = prefs[Keys.THIRD_PARTY_COOKIES] ?: false,
                saveHistory = prefs[Keys.SAVE_HISTORY] ?: true,
            )
        }

    suspend fun setSearchEngine(engine: SearchEngine) {
        context.settingsDataStore.edit { it[Keys.SEARCH_ENGINE] = engine.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setHomepage(url: String) {
        context.settingsDataStore.edit { it[Keys.HOMEPAGE] = url.trim() }
    }

    suspend fun setJavaScriptEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.JAVASCRIPT] = enabled }
    }

    suspend fun setThirdPartyCookiesEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.THIRD_PARTY_COOKIES] = enabled }
    }

    suspend fun setSaveHistory(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.SAVE_HISTORY] = enabled }
    }
}
