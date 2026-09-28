package com.therxmv.otaupdates.presentation.receiver

import android.app.DownloadManager
import android.app.DownloadManager.EXTRA_DOWNLOAD_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import com.therxmv.common.SharedPrefs.SHARED_PREFS
import com.therxmv.common.SharedPrefs.SHARED_PREFS_IS_UPDATE_DOWNLOADED
import com.therxmv.common.SharedPrefs.SHARED_PREFS_UPDATE_DOWNLOAD_ID

class DownloadCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            handleDownloadCompleted(context, intent)
        }
    }

    private fun handleDownloadCompleted(context: Context, intent: Intent) {
        val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val id = intent.getLongExtra(EXTRA_DOWNLOAD_ID, -1L)
        val expectedId = sharedPrefs.getLong(SHARED_PREFS_UPDATE_DOWNLOAD_ID, -1L)

        // Ignore unrelated downloads: only our enqueued id updates the flag.
        if (id != -1L && id == expectedId) {
            sharedPrefs.edit(commit = true) {
                putBoolean(SHARED_PREFS_IS_UPDATE_DOWNLOADED, isDownloadSuccessful(context, id))
            }
        }
    }

    private fun isDownloadSuccessful(context: Context, downloadId: Long): Boolean {
        val downloadManager = context.getSystemService(DownloadManager::class.java)
        val cursor = downloadManager
            ?.query(DownloadManager.Query().setFilterById(downloadId))
            ?: return false

        return cursor.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) ==
                    DownloadManager.STATUS_SUCCESSFUL
            } else {
                Log.w(TAG, "Download $downloadId not found in DownloadManager")
                false
            }
        }
    }

    private companion object {
        const val TAG = "DownloadCompletedReceiver"
    }
}
