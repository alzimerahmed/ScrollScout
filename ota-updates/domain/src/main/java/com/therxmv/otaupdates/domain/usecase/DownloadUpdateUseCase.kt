package com.therxmv.otaupdates.domain.usecase

import com.therxmv.otaupdates.domain.models.LatestReleaseModel
import com.therxmv.otaupdates.domain.repository.DownloaderApi
import javax.inject.Inject

class DownloadUpdateUseCase @Inject constructor(
    private val downloaderApi: DownloaderApi,
) {
    /**
     * @return enqueued DownloadManager id, or -1 when the download could not be started.
     */
    operator fun invoke(latestReleaseModel: LatestReleaseModel): Long =
        downloaderApi.downloadFile(latestReleaseModel)
}