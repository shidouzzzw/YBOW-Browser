package com.ybow.browser.data

import android.content.Context
import com.ybow.browser.data.local.Bookmark
import com.ybow.browser.data.local.HistoryEntry
import com.ybow.browser.data.local.Shortcut
import com.ybow.browser.data.local.YbowDatabase
import com.ybow.browser.util.UrlTools
import kotlinx.coroutines.flow.Flow

/**
 * Bookmarks, history and home screen shortcuts. All of them live only in the local
 * Room database.
 */
class BrowserRepository(context: Context) {

    private val database = YbowDatabase.get(context)
    private val historyDao = database.historyDao()
    private val bookmarkDao = database.bookmarkDao()
    private val shortcutDao = database.shortcutDao()

    val history: Flow<List<HistoryEntry>> = historyDao.observeAll()
    val recentHistory: Flow<List<HistoryEntry>> = historyDao.observeRecent(12)
    val bookmarks: Flow<List<Bookmark>> = bookmarkDao.observeAll()
    val shortcuts: Flow<List<Shortcut>> = shortcutDao.observeAll()

    fun searchHistory(query: String): Flow<List<HistoryEntry>> =
        if (query.isBlank()) historyDao.observeAll() else historyDao.search(query)

    fun searchBookmarks(query: String): Flow<List<Bookmark>> =
        if (query.isBlank()) bookmarkDao.observeAll() else bookmarkDao.search(query)

    /**
     * Stores a visit. Repeated visits to the same URL inside the merge window update the
     * existing row instead of flooding the list with duplicates.
     */
    suspend fun recordVisit(url: String, title: String) {
        if (!UrlTools.isHttpUrl(url)) return
        val now = System.currentTimeMillis()
        val label = title.trim().ifBlank { UrlTools.displayUrl(url) }
        val existing = historyDao.findLatestForUrl(url)
        if (existing != null && now - existing.visitedAt <= VISIT_MERGE_WINDOW_MS) {
            historyDao.update(existing.copy(title = label, visitedAt = now))
        } else {
            historyDao.insert(HistoryEntry(url = url, title = label, visitedAt = now))
        }
    }

    suspend fun deleteHistoryEntry(id: Long) = historyDao.deleteById(id)

    suspend fun clearHistory() = historyDao.clear()

    /** Returns true when the URL is bookmarked after the toggle. */
    suspend fun toggleBookmark(url: String, title: String): Boolean {
        if (!UrlTools.isHttpUrl(url)) return false
        return if (bookmarkDao.countByUrl(url) > 0) {
            bookmarkDao.deleteByUrl(url)
            false
        } else {
            bookmarkDao.insert(
                Bookmark(url = url, title = title.trim().ifBlank { UrlTools.displayUrl(url) })
            )
            true
        }
    }

    suspend fun removeBookmark(bookmark: Bookmark) = bookmarkDao.deleteById(bookmark.id)

    suspend fun removeBookmarkByUrl(url: String) = bookmarkDao.deleteByUrl(url)

    suspend fun isBookmarked(url: String): Boolean = bookmarkDao.countByUrl(url) > 0

    // ------------------------------------------------------------- shortcuts

    /** Returns false when the URL is already saved as a shortcut. */
    suspend fun addShortcut(url: String, title: String): Boolean {
        if (!UrlTools.isHttpUrl(url)) return false
        if (shortcutDao.countByUrl(url) > 0) return false
        shortcutDao.insert(
            Shortcut(url = url, title = title.trim().ifBlank { UrlTools.displayUrl(url) })
        )
        return true
    }

    suspend fun updateShortcut(shortcut: Shortcut) = shortcutDao.update(shortcut)

    suspend fun deleteShortcut(shortcut: Shortcut) = shortcutDao.deleteById(shortcut.id)

    private companion object {
        const val VISIT_MERGE_WINDOW_MS = 60_000L
    }
}
