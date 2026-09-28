package com.therxmv.otaupdates.data.models

import com.google.gson.annotations.SerializedName
import com.therxmv.otaupdates.domain.models.LatestReleaseModel

data class LatestReleaseJson(
    @SerializedName("tag_name") val tagName: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("body") val changeLog: String?,
    @SerializedName("assets") val assets: List<LatestReleaseAssetJson>?,
)

data class LatestReleaseAssetJson(
    @SerializedName("name") val fileName: String?,
    @SerializedName("content_type") val contentType: String?,
    @SerializedName("browser_download_url") val downloadUrl: String?,
)

/**
 * Null when the release ships no usable APK asset (draft/source-only releases,
 * incomplete payloads). Prefers the universal APK from ABI splits so every
 * device installs a compatible build; falls back to the first .apk otherwise.
 */
fun LatestReleaseJson.toDomain(): LatestReleaseModel? =
    assets.orEmpty().pickApkAsset()?.let { apk ->
        LatestReleaseModel(
            version = tagName.takeUnless { it.isNullOrBlank() } ?: name.orEmpty(),
            changeLog = changeLog.orEmpty(),
            fileName = apk.fileName.orEmpty(),
            contentType = apk.contentType.orEmpty(),
            downloadUrl = apk.downloadUrl.orEmpty(),
        )
    }

private fun List<LatestReleaseAssetJson>.pickApkAsset(): LatestReleaseAssetJson? =
    filter { asset ->
        asset.fileName?.endsWith(".apk") == true && asset.downloadUrl.isNullOrBlank().not()
    }.let { apks ->
        apks.firstOrNull { it.fileName.orEmpty().contains("universal") } ?: apks.firstOrNull()
    }
