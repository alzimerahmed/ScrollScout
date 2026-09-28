package com.therxmv.dirolreader.ui.relevance.viewmodel

import app.cash.turbine.test
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.usecase.channel.AdjustChannelWeightUseCase
import com.therxmv.dirolreader.domain.usecase.channel.GetChannelsUseCase
import com.therxmv.dirolreader.domain.usecase.channel.ResetChannelWeightsUseCase
import com.therxmv.dirolreader.ui.channels.viewmodel.MainDispatcherRule
import com.therxmv.dirolreader.ui.relevance.viewmodel.utils.RelevanceUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RelevanceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getChannels: GetChannelsUseCase = mockk()
    private val adjustWeight: AdjustChannelWeightUseCase = mockk(relaxed = true)
    private val resetWeights: ResetChannelWeightsUseCase = mockk(relaxed = true)

    private fun createViewModel(
        channels: List<ChannelModel>,
        refreshFails: Boolean = false,
    ): RelevanceViewModel {
        every { getChannels.observe() } returns flowOf(channels)
        if (refreshFails) {
            coEvery { getChannels.refresh() } throws java.io.IOException("offline")
        } else {
            coEvery { getChannels.refresh() } returns channels
        }
        return RelevanceViewModel(
            getChannels = getChannels,
            adjustWeight = adjustWeight,
            resetWeights = resetWeights,
        )
    }

    @Test
    fun `error state when refresh fails with no cached channels`() = runTest {
        val viewModel = createViewModel(channels = emptyList(), refreshFails = true)

        viewModel.uiState.test {
            assertTrue(awaitItem() is RelevanceUiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ready state sorts by rating descending and scales to max`() = runTest {
        val viewModel = createViewModel(
            channels = listOf(
                ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L, rating = 5),
                ChannelModel(id = 2L, unreadCount = 0, lastReadMessageId = 0L, rating = 10),
                ChannelModel(id = 3L, unreadCount = 0, lastReadMessageId = 0L, rating = 0),
            ),
        )

        viewModel.uiState.test {
            val ready = awaitItem() as RelevanceUiState.Ready
            assertEquals(listOf(2L, 1L, 3L), ready.items.map { it.id })
            assertEquals(10, ready.items.first().maxRating)
            assertTrue(ready.canReset)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `canReset is false when all weights are zero`() = runTest {
        val viewModel = createViewModel(
            channels = listOf(
                ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L, rating = 0),
            ),
        )

        viewModel.uiState.test {
            val ready = awaitItem() as RelevanceUiState.Ready
            assertFalse(ready.canReset)
            assertEquals(1, ready.items.first().maxRating)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty state after refresh with no channels`() = runTest {
        val viewModel = createViewModel(channels = emptyList())

        viewModel.uiState.test {
            assertTrue(awaitItem() is RelevanceUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `adjust delegates delta to use case`() = runTest {
        val viewModel = createViewModel(
            channels = listOf(
                ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L, rating = 0),
            ),
        )

        viewModel.onAdjust(id = 1L, delta = -1)

        coVerify(exactly = 1) { adjustWeight(1L, -1) }
    }

    @Test
    fun `reset delegates to use case`() = runTest {
        val viewModel = createViewModel(
            channels = listOf(
                ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L, rating = 3),
            ),
        )

        viewModel.onReset()

        coVerify(exactly = 1) { resetWeights() }
    }
}
