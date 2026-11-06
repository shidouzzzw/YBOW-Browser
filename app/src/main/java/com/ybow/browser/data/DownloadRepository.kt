package com.ybow.browser.data

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.webkit.URLUtil
import com.ybow.browser.data.local.DownloadRecord
import com.ybow.browser.data.local.YbowDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

data class DownloadStatus(
    val state: Int,
    val reason: Int,
    val bytesDownloaded: Long,
    val totalBytes: Long,
) {
    val isActive: Boolean
        get() = state == DownloadManager.STATUS_RUNNING ||
            state == DownloadManager.STATUS_PENDING ||
            state == DownloadManager.STATUS_PAUSED

    val isSuccessful: Boolean get() = state == DownloadManager.STATUS_SUCCESSFUL

    val progress: Float
        get() = when {
            isSuccessful -> 1f
            totalBytes > 0L -> (bytesDownloaded.toDouble() / totalBytes.toDouble()).toFloat()
                .coerceIn(0f, 1f)
            else -> 0f
        }
}

/**
 * Real file downloads backed by the platform DownloadManager, with the download list
 * kept in the local Room database.
 *
 * No storage permission is requested: on Android 10+ files are written to the public
 * Downloads folder (scoped storage) and on older releases to the app-specific Downloads
 * folder, both writable without user grants.
 */
class DownloadRepository(private val context: Context) {

    private val dao = YbowDatabase.get(context).downloadDao()
    private val manager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val records: Flow<List<DownloadRecord>> = dao.observeAll()

    suspend fun enqueue(
        url: String,
        userAgent: String?,
        cookie: String?,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long,
    ): DownloadRecord? = withContext(Dispatchers.IO) {
        if (!url.startsWith("http", ignoreCase = true)) return@withContext null
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
            .ifBlank { "download.bin" }

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(false)
            if (!TextUtils.isEmpty(userAgent)) addRequestHeader("User-Agent", userAgent)
            if (!TextUtils.isEmpty(cookie)) addRequestHeader("Cookie", cookie)
            if (!TextUtils.isEmpty(contentDisposition)) {
                addRequestHeader("Content-Disposition", contentDisposition)
            }
            if (!mimeType.isNullOrBlank()) setMimeType(mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            } else {
                setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            }
        }

        val systemId = try {
            manager.enqueue(request)
        } catch (error: Exception) {
            return@withContext null
        }

        val record = DownloadRecord(
            systemId = systemId,
            url = url,
            fileName = fileName,
            mimeType = mimeType,
            contentLength = contentLength,
        )
        dao.insert(record)
        record
    }

    fun status(systemId: Long): DownloadStatus? {
        val cursor = try {
            manager.query(DownloadManager.Query().setFilterById(systemId))
        } catch (error: Exception) {
            null
        } ?: return null
        cursor.use {
            if (!it.moveToFirst()) return null
            val stateIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val downloadedIndex = it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val totalIndex = it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            val reasonIndex = it.getColumnIndex(DownloadManager.COLUMN_REASON)
            if (stateIndex < 0) return null
            return DownloadStatus(
                state = it.getInt(stateIndex),
                reason = if (reasonIndex >= 0) it.getInt(reasonIndex) else 0,
                bytesDownloaded = if (downloadedIndex >= 0) it.getLong(downloadedIndex) else 0L,
                totalBytes = if (totalIndex >= 0) it.getLong(totalIndex) else 0L,
            )
        }
    }

    fun openIntent(record: DownloadRecord): Intent? {
        val uri = manager.getUriForDownloadedFile(record.systemId) ?: return null
        val type = record.mimeType
            ?: manager.getMimeTypeForDownloadedFile(record.systemId)
            ?: "*/*"
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, type)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    suspend fun remove(record: DownloadRecord) = withContext(Dispatchers.IO) {
        runCatching { manager.remove(record.systemId) }
        dao.deleteById(record.id)
    }
}
