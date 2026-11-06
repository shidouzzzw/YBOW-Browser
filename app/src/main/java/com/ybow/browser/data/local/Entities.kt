package com.ybow.browser.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "history_entries", indices = [Index(value = ["visitedAt"])])
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val url: String,
    val title: String,
    val visitedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "bookmarks", indices = [Index(value = ["url"], unique = true)])
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val url: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "downloads")
data class DownloadRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val systemId: Long,
    val url: String,
    val fileName: String,
    val mimeType: String?,
    val contentLength: Long,
    val createdAt: Long = System.currentTimeMillis(),
)
