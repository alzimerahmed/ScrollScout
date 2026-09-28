package com.therxmv.otaupdates.data.models

import com.google.gson.annotations.SerializedName
import com.therxmv.otaupdates.domain.models.LatestReleaseModel

data class LatestReleaseJson(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("name") val name: String?,
    @SerializedName("body") val changeLog: String?,
    @SerializedName("assets") val assets: List<LatestReleaseAssetJson>,
)

data class LatestReleaseAssetJson(
    @SerializedName("name") val fileName: String,
    @SerializedName("content_type") val contentType: String,
    @SerializedName("browser_download_url") val downloadUrl: String,
)

/**
 * Null when the release ships no APK asset (draft/source-only releases).
 * Prefers the universal APK from ABI splits so every device installs
 * a compatible build; falls back to the first .apk otherwise.
 */
fun LatestReleaseJson.toDomain(): LatestReleaseModel? {
    val apk = assets.firstOrNull {
        it.fileName.endsWith(".apk") && it.fileName.contains("universal")
    } ?: assets.firstOrNull {
        it.fileName.endsWith(".apk")
    } ?: return null

    return LatestReleaseModel(
        version = tagName.ifBlank { name.orEmpty() },
        changeLog = changeLog.orEmpty(),
        fileName = apk.fileName,
        contentType = apk.contentType,
        downloadUrl = apk.downloadUrl,
    )
}
