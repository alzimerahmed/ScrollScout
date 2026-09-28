package com.therxmv.dirolreader.ui.channels.viewmodel

import app.cash.turbine.test
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.usecase.channel.GetChannelsUseCase
import com.therxmv.dirolreader.domain.usecase.channel.ReorderChannelUseCase
import com.therxmv.dirolreader.domain.usecase.channel.SetChannelGroupUseCase
import com.therxmv.dirolreader.domain.usecase.channel.SetChannelMutedUseCase
import com.therxmv.dirolreader.ui.channels.viewmodel.utils.ChannelsUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ChannelsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getChannels: GetChannelsUseCase = mockk()
    private val setChannelMuted: SetChannelMutedUseCase = mockk()
    private val reorderChannel: ReorderChannelUseCase = mockk(relaxed = true)
    private val setChannelGroup: SetChannelGroupUseCase = mockk(relaxed = true)

    private fun createViewModel(
        channels: List<ChannelModel> = emptyList(),
        muteResult: Boolean = true,
    ): ChannelsViewModel {
        every { getChannels.observe() } returns flowOf(channels)
        coEvery { getChannels.refresh() } returns channels
        coEvery { setChannelMuted(any(), any()) } returns muteResult
        return ChannelsViewModel(
            getChannels = getChannels,
            setChannelMuted = setChannelMuted,
            reorderChannel = reorderChannel,
            setChannelGroup = setChannelGroup,
        )
    }

    @Test
    fun `ready state exposes channels sorted by order then title`() = runTest {
        val viewModel = createViewModel(
            channels = listOf(
                ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L, order = 2, title = "B"),
                ChannelModel(id = 2L, unreadCount = 3, lastReadMessageId = 0L, order = 1, title = "A"),
            ),
        )

        viewModel.uiState.test {
            val ready = awaitItem() as ChannelsUiState.Ready
            assertEquals(listOf(2L, 1L), ready.channels.map { it.id })
            assertEquals(3, ready.channels.first().unreadCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty state after refresh with no channels`() = runTest {
        val viewModel = createViewModel(channels = emptyList())

        viewModel.uiState.test {
            assertTrue(awaitItem() is ChannelsUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle mute delegates to use case`() = runTest {
        val viewModel = createViewModel()

        viewModel.onToggleMute(id = 5L, isMuted = true)

        coVerify(exactly = 1) { setChannelMuted(5L, true) }
    }

    @Test
    fun `failed mute emits failure event`() = runTest {
        val viewModel = createViewModel(muteResult = false)

        viewModel.muteFailed.test {
            viewModel.onToggleMute(id = 5L, isMuted = true)
            assertEquals(5L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `move up swaps channel with previous one and normalizes orders`() = runTest {
        val viewModel = createViewModel(
            channels = listOf(
                ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L, order = 0, title = "A"),
                ChannelModel(id = 2L, unreadCount = 0, lastReadMessageId = 0L, order = 1, title = "B"),
                ChannelModel(id = 3L, unreadCount = 0, lastReadMessageId = 0L, order = 2, title = "C"),
            ),
        )

        viewModel.onMove(id = 2L, up = true)

        coVerify(exactly = 1) { reorderChannel(2L, 0) }
        coVerify(exactly = 1) { reorderChannel(1L, 1) }
        coVerify(exactly = 0) { reorderChannel(3L, any()) }
    }

    @Test
    fun `move at boundary does not reorder`() = runTest {
        val viewModel = createViewModel(
            channels = listOf(
                ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L, order = 0, title = "A"),
            ),
        )

        viewModel.onMove(id = 1L, up = true)

        coVerify(exactly = 0) { reorderChannel(any(), any()) }
    }

    @Test
    fun `set group delegates to use case`() = runTest {
        val viewModel = createViewModel()

        viewModel.onSetGroup(id = 4L, group = "tech")

        coVerify(exactly = 1) { setChannelGroup(4L, "tech") }
    }
}
