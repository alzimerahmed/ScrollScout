package com.therxmv.otaupdates.data.source.remote

import com.therxmv.otaupdates.data.models.LatestReleaseJson
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LatestReleaseRemoteDataSourceTest {

    private val mockGithubApiService = mockk<GithubApiService> {
        coEvery { getLatestRelease(any(), any()) } returns LatestReleaseJson(
            tagName = "v1.0.0",
            name = "v1.0.0",
            changeLog = "changeLog",
            assets = emptyList(),
        )
    }
    private val mockDispatcher = StandardTestDispatcher()

    private val systemUnderTest = LatestReleaseRemoteDataSource(
        apiService = mockGithubApiService,
        ioDispatcher = mockDispatcher,
    )

    @Before
    fun setup() {
        Dispatchers.setMain(mockDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `returns latest release data`() = runTest {
        val result = systemUnderTest.getLatestRelease()

        coVerify { mockGithubApiService.getLatestRelease(any(), any()) }
        result.shouldBeInstanceOf<LatestReleaseJson>()
    }

    @Test
    fun `returns null when network request fails`() = runTest {
        coEvery { mockGithubApiService.getLatestRelease(any(), any()) } throws IOException()

        val result = systemUnderTest.getLatestRelease()

        result.shouldBeNull()
    }
}
