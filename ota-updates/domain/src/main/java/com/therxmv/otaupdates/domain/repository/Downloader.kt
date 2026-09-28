package com.therxmv.otaupdates.domain.repository

import com.therxmv.otaupdates.domain.models.LatestReleaseModel

interface DownloaderApi {
    /**
     * @return enqueued DownloadManager id, or -1 when the download could not be started.
     */
    fun downloadFile(latestReleaseModel: LatestReleaseModel): Long

    /**
     * True only when the completed download belongs to this exact release
     * (fileName + version), the file exists, and the completion flag is set.
     * A stale flag from a previous release is cleared as a side effect.
     */
    fun isDownloaded(latestReleaseModel: LatestReleaseModel): Boolean
}
