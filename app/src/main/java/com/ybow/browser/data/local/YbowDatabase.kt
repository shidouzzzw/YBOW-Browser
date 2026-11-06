package com.ybow.browser.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HistoryEntry::class, Bookmark::class, DownloadRecord::class],
    version = 1,
    exportSchema = false,
)
abstract class YbowDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun downloadDao(): DownloadDao

    companion object {
        private const val DATABASE_NAME = "ybow.db"

        @Volatile
        private var instance: YbowDatabase? = null

        fun get(context: Context): YbowDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    YbowDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { instance = it }
            }
    }
}
