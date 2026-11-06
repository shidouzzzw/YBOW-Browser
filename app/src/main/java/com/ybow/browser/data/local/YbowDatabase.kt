package com.ybow.browser.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [HistoryEntry::class, Bookmark::class, Shortcut::class, DownloadRecord::class],
    version = 2,
    exportSchema = false,
)
abstract class YbowDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun shortcutDao(): ShortcutDao

    abstract fun downloadDao(): DownloadDao

    companion object {
        private const val DATABASE_NAME = "ybow.db"

        /** Adds the shortcuts table without touching the existing history and bookmarks. */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `shortcuts` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`url` TEXT NOT NULL, " +
                        "`title` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_shortcuts_url` ON `shortcuts` (`url`)"
                )
            }
        }

        @Volatile
        private var instance: YbowDatabase? = null

        fun get(context: Context): YbowDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    YbowDatabase::class.java,
                    DATABASE_NAME,
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}
