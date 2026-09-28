package com.therxmv.dirolreader.ui.search.viewmodel

import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.usecase.NewsViewModelUseCases
import com.therxmv.dirolreader.domain.usecase.channel.UpdateChannelRatingUseCase
import com.therxmv.dirolreader.domain.usecase.message.DownloadMediaAndGetPathUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetMessagePagingUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetReadablePostTimeUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetSavedMessagesUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetUnreadChannelsFlowUseCase
import com.therxmv.dirolreader.domain.usecase.message.MarkAllAsReadUseCase
import com.therxmv.dirolreader.domain.usecase.message.MarkMessageAsReadUseCase
import com.therxmv.dirolreader.domain.usecase.message.SaveMessageUseCase
import com.therxmv.dirolreader.domain.usecase.message.SearchMessagesUseCase
import com.therxmv.dirolreader.domain.usecase.message.TranslateTextUseCase
import com.therxmv.dirolreader.domain.usecase.user.GetCurrentUserUseCase
import com.therxmv.dirolreader.ui.news.viewmodel.MainDispatcherRule
import com.therxmv.dirolreader.ui.search.viewmodel.utils.SearchUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val searchMessages: SearchMessagesUseCase = mockk()
    private val getReadablePostTime: GetReadablePostTimeUseCase = mockk()

    private fun message(id: Long) = MessageModel(
        id = id,
        channelData = ChannelData(id = 1L, rating = 0, name = "channel"),
        timestamp = 0,
        text = "post text",
        mediaList = null,
    )

    private fun buildViewModel() = SearchViewModel(
        useCases = NewsViewModelUseCases(
            getCurrentUser = mockk<GetCurrentUserUseCase>(),
            getNewsPaging = mockk<GetMessagePagingUseCase>().apply {
                every { this@apply() } returns flowOf()
            },
            updateChannelRating = mockk<UpdateChannelRatingUseCase>(relaxed = true),
            downloadMediaAndGetPath = mockk<DownloadMediaAndGetPathUseCase>(relaxed = true),
            markMessageAsRead = mockk<MarkMessageAsReadUseCase>(relaxed = true),
            getReadablePostTime = getReadablePostTime,
            getUnreadChannelsFlow = mockk<GetUnreadChannelsFlowUseCase>(),
            saveMessage = mockk<SaveMessageUseCase>(relaxed = true),
            getSavedMessages = mockk<GetSavedMessagesUseCase>().apply {
                every { this@apply() } returns emptyFlow()
            },
            markAllAsRead = mockk<MarkAllAsReadUseCase>(relaxed = true),
            translateText = mockk<TranslateTextUseCase>(),
            searchMessages = searchMessages,
        ),
    )

    @Test
    fun `initial state is Initial`() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        runCurrent()

        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun `query with results emits Ready after debounce`() = runTest(testDispatcher) {
        every { getReadablePostTime(any()) } returns "time"
        coEvery { searchMessages("kotlin") } returns listOf(message(1L))
        val viewModel = buildViewModel()

        viewModel.onQueryChange("kotlin")
        advanceTimeBy(300)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(true, state is SearchUiState.Ready)
        assertEquals(1, (state as SearchUiState.Ready).results.size)
        assertEquals("channel", state.results.first().channelName)
        assertEquals("time", state.results.first().time)
    }

    @Test
    fun `query without results emits NoResults`() = runTest(testDispatcher) {
        coEvery { searchMessages("zzz") } returns emptyList()
        val viewModel = buildViewModel()

        viewModel.onQueryChange("zzz")
        advanceTimeBy(300)
        runCurrent()

        assertEquals(SearchUiState.NoResults, viewModel.uiState.value)
    }

    @Test
    fun `search failure emits Error`() = runTest(testDispatcher) {
        coEvery { searchMessages("boom") } throws RuntimeException("db closed")
        val viewModel = buildViewModel()

        viewModel.onQueryChange("boom")
        advanceTimeBy(300)
        runCurrent()

        assertEquals(SearchUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `blank query resets to Initial`() = runTest(testDispatcher) {
        every { getReadablePostTime(any()) } returns "time"
        coEvery { searchMessages("kotlin") } returns listOf(message(1L))
        val viewModel = buildViewModel()

        viewModel.onQueryChange("kotlin")
        advanceTimeBy(300)
        runCurrent()

        viewModel.onQueryChange("   ")
        advanceTimeBy(300)
        runCurrent()

        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
    }
}
