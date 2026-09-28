package com.therxmv.otaupdates.data.source.repository

import com.therxmv.otaupdates.data.models.LatestReleaseAssetJson
import com.therxmv.otaupdates.data.models.LatestReleaseJson
import com.therxmv.otaupdates.data.repository.LatestReleaseRepositoryImpl
import com.therxmv.otaupdates.data.source.remote.LatestReleaseRemoteDataSource
import com.therxmv.otaupdates.domain.models.LatestReleaseModel
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LatestReleaseRepositoryImplTest {

    private val releaseJson = LatestReleaseJson(
        tagName = "v1.0.0",
        name = "Release 1.0.0",
        changeLog = "changeLog",
        assets = listOf(
            LatestReleaseAssetJson(
                fileName = "app-universal-release.apk",
                contentType = "type",
                downloadUrl = "url",
            ),
        ),
    )
    private val mockLatestReleaseRemoteDataSource = mockk<LatestReleaseRemoteDataSource> {
        coEvery { getLatestRelease() } returns releaseJson
    }

    private val systemUnderTest = LatestReleaseRepositoryImpl(
        latestReleaseRemoteDataSource = mockLatestReleaseRemoteDataSource,
    )

    @Test
    fun `returns converted release model`() = runTest {
        val expectedModel = LatestReleaseModel(
            version = releaseJson.tagName,
            changeLog = releaseJson.changeLog,
            fileName = releaseJson.assets.first().fileName,
            contentType = releaseJson.assets.first().contentType,
            downloadUrl = releaseJson.assets.first().downloadUrl,
        )

        val result = systemUnderTest.getLatestRelease()

        result shouldBe expectedModel
    }

    @Test
    fun `prefers universal apk over abi splits`() = runTest {
        coEvery { mockLatestReleaseRemoteDataSource.getLatestRelease() } returns releaseJson.copy(
            assets = listOf(
                LatestReleaseAssetJson("app-arm64-v8a-release.apk", "type", "url-arm64"),
                LatestReleaseAssetJson("app-universal-release.apk", "type", "url-universal"),
            ),
        )

        val result = systemUnderTest.getLatestRelease()

        result?.downloadUrl shouldBe "url-universal"
    }

    @Test
    fun `returns null when release has no apk asset`() = runTest {
        coEvery { mockLatestReleaseRemoteDataSource.getLatestRelease() } returns releaseJson.copy(
            assets = listOf(
                LatestReleaseAssetJson("source.zip", "type", "url"),
            ),
        )

        systemUnderTest.getLatestRelease().shouldBeNull()
    }
}
