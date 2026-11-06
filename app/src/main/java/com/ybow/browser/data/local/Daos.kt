package com.ybow.browser.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history_entries ORDER BY visitedAt DESC")
    fun observeAll(): Flow<List<HistoryEntry>>

    @Query("SELECT * FROM history_entries ORDER BY visitedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<HistoryEntry>>

    @Query(
        "SELECT * FROM history_entries WHERE url LIKE '%' || :query || '%' " +
            "OR title LIKE '%' || :query || '%' ORDER BY visitedAt DESC LIMIT 300"
    )
    fun search(query: String): Flow<List<HistoryEntry>>

    @Query("SELECT * FROM history_entries WHERE url = :url ORDER BY visitedAt DESC LIMIT 1")
    suspend fun findLatestForUrl(url: String): HistoryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntry): Long

    @Update
    suspend fun update(entry: HistoryEntry)

    @Query("DELETE FROM history_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM history_entries")
    suspend fun clear()
}

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Bookmark>>

    @Query(
        "SELECT * FROM bookmarks WHERE url LIKE '%' || :query || '%' " +
            "OR title LIKE '%' || :query || '%' ORDER BY createdAt DESC"
    )
    fun search(query: String): Flow<List<Bookmark>>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE url = :url")
    suspend fun countByUrl(url: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookmark: Bookmark): Long

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteByUrl(url: String)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM bookmarks")
    suspend fun clear()
}

@Dao
interface ShortcutDao {

    @Query("SELECT * FROM shortcuts ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<Shortcut>>

    @Query("SELECT COUNT(*) FROM shortcuts WHERE url = :url")
    suspend fun countByUrl(url: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shortcut: Shortcut): Long

    @Update
    suspend fun update(shortcut: Shortcut)

    @Query("DELETE FROM shortcuts WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DownloadRecord>>

    @Insert
    suspend fun insert(record: DownloadRecord): Long

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM downloads")
    suspend fun clear()
}
