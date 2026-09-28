package com.therxmv.otaupdates.data.source.remote

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.content.edit
import androidx.core.net.toUri
import com.therxmv.common.SharedPrefs.SHARED_PREFS
import com.therxmv.common.SharedPrefs.SHARED_PREFS_UPDATE_DOWNLOAD_ID
import com.therxmv.otaupdates.domain.models.LatestReleaseModel
import com.therxmv.otaupdates.domain.repository.DownloaderApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LatestReleaseDownloader @Inject constructor(
    @ApplicationContext context: Context,
) : DownloaderApi {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    override fun downloadFile(latestReleaseModel: LatestReleaseModel): Long {
        val request = DownloadManager.Request(latestReleaseModel.downloadUrl.toUri())
            .setMimeType(latestReleaseModel.contentType)
            .setTitle(latestReleaseModel.fileName.replace(".apk", ""))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, latestReleaseModel.fileName)

        val manager = downloadManager
        if (manager == null) {
            Log.e(TAG, "DownloadManager is unavailable")
            return INVALID_DOWNLOAD_ID
        }

        val id = try {
            manager.enqueue(request)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Update download was rejected", e)
            INVALID_DOWNLOAD_ID
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing permission to enqueue update download", e)
            INVALID_DOWNLOAD_ID
        }

        if (id != INVALID_DOWNLOAD_ID) {
            // Remember the id so DownloadCompletedReceiver can ignore other apps' downloads.
            sharedPrefs.edit(commit = true) {
                putLong(SHARED_PREFS_UPDATE_DOWNLOAD_ID, id)
            }
        }

        return id
    }

    private companion object {
        const val TAG = "LatestReleaseDownloader"
        const val INVALID_DOWNLOAD_ID = -1L
    }
}
