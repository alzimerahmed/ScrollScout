package com.therxmv.otaupdates.presentation.viewmodel

import com.therxmv.otaupdates.domain.models.LatestReleaseModel
import com.therxmv.otaupdates.domain.usecase.DownloadUpdateUseCase
import com.therxmv.otaupdates.domain.usecase.GetLatestReleaseUseCase
import com.therxmv.otaupdates.presentation.viewmodel.utils.OtaUiEvent
import com.therxmv.otaupdates.presentation.viewmodel.utils.OtaUiState
import com.therxmv.sharedpreferences.repository.AppSharedPrefsRepository
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtaViewModelTest {

    private val releaseModel = LatestReleaseModel(
        version = "v1.0.0",
        changeLog = "changeLog",
        fileName = "fileName",
        contentType = "contentType",
        downloadUrl = "downloadUrl",
    )
    private val mockGetLatestReleaseUseCase = mockk<GetLatestReleaseUseCase> {
        coEvery { this@mockk.invoke() } returns null
    }
    private val mockDownloadUpdateUseCase = mockk<DownloadUpdateUseCase>(relaxed = true)
    private val mockAppSharedPrefsRepository = mockk<AppSharedPrefsRepository>(relaxed = true)
    private val mockVersionCode = 200

    private lateinit var systemUnderTest: OtaViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        assumeViewModelCreated()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `InitialState on viewModel init`() = runTest {
        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.InitialState>()
    }

    @Test
    fun `set DownloadUpdate when latest release is not downloaded yet`() = runTest {
        coEvery { mockGetLatestReleaseUseCase.invoke() } returns releaseModel.copy(version = "v5.0.0")
        every { mockDownloadUpdateUseCase.isDownloaded(any()) } returns false

        advanceUntilIdle()

        coVerify { mockGetLatestReleaseUseCase.invoke() }
        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.DownloadUpdate>()
    }

    @Test
    fun `set Downloaded when latest release apk is already downloaded`() = runTest {
        coEvery { mockGetLatestReleaseUseCase.invoke() } returns releaseModel.copy(version = "v5.0.0")
        every { mockDownloadUpdateUseCase.isDownloaded(any()) } returns true

        advanceUntilIdle()

        coVerify { mockGetLatestReleaseUseCase.invoke() }
        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.Downloaded>()
    }

    @Test
    fun `set Error when release fetch fails`() = runTest {
        advanceUntilIdle()

        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.Error>()
    }

    @Test
    fun `retries release check on Retry event`() = runTest {
        advanceUntilIdle()
        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.Error>()

        coEvery { mockGetLatestReleaseUseCase.invoke() } returns releaseModel.copy(version = "v5.0.0")
        systemUnderTest.onEvent(OtaUiEvent.Retry)
        advanceUntilIdle()

        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.DownloadUpdate>()
    }

    @Test
    fun `set Error when update download cannot be enqueued`() = runTest {
        coEvery { mockGetLatestReleaseUseCase.invoke() } returns releaseModel
        every { mockDownloadUpdateUseCase.invoke(any()) } returns -1L

        advanceUntilIdle()

        systemUnderTest.onEvent(OtaUiEvent.DownloadUpdate(releaseModel))

        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.Error>()
    }

    @Test
    fun `set Downloading when update download is enqueued`() = runTest {
        coEvery { mockGetLatestReleaseUseCase.invoke() } returns releaseModel
        every { mockDownloadUpdateUseCase.invoke(any()) } returns 42L
        every { mockAppSharedPrefsRepository.isUpdateDownloadedChangeListener(any()) } returns mockk()

        advanceUntilIdle()

        systemUnderTest.onEvent(OtaUiEvent.DownloadUpdate(releaseModel))

        systemUnderTest.uiState.value.shouldBeInstanceOf<OtaUiState.Downloading>()
    }

    private fun assumeViewModelCreated() {
        systemUnderTest = OtaViewModel(
            getLatestReleaseUseCase = mockGetLatestReleaseUseCase,
            downloadUpdateUseCase = mockDownloadUpdateUseCase,
            appSharedPrefsRepository = mockAppSharedPrefsRepository,
            versionCode = mockVersionCode,
            ioDispatcher = testDispatcher,
        )
    }
}
