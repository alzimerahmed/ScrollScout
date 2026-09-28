package com.therxmv.dirolreader.ui.news.viewmodel

import androidx.paging.PagingData
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.models.UserModel
import com.therxmv.dirolreader.domain.usecase.NewsViewModelUseCases
import com.therxmv.dirolreader.domain.usecase.channel.UpdateChannelRatingUseCase
import com.therxmv.dirolreader.domain.usecase.message.DownloadMediaAndGetPathUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetMessagePagingUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetReadablePostTimeUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetUnreadChannelsFlowUseCase
import com.therxmv.dirolreader.domain.usecase.message.MarkMessageAsReadUseCase
import com.therxmv.dirolreader.domain.usecase.user.GetCurrentUserUseCase
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCurrentUser: GetCurrentUserUseCase = mockk()
    private val getNewsPaging: GetMessagePagingUseCase = mockk()
    private val updateChannelRating: UpdateChannelRatingUseCase = mockk(relaxed = true)
    private val downloadMediaAndGetPath: DownloadMediaAndGetPathUseCase = mockk(relaxed = true)
    private val markMessageAsRead: MarkMessageAsReadUseCase = mockk(relaxed = true)
    private val getReadablePostTime: GetReadablePostTimeUseCase = mockk(relaxed = true)
    private val getUnreadChannelsFlow: GetUnreadChannelsFlowUseCase = mockk()

    private lateinit var viewModel: FeedViewModel

    private val unreadChannels = MutableStateFlow(
        listOf(
            ChannelModel(id = 5L, unreadCount = 1, lastReadMessageId = 0L, rating = 100),
            ChannelModel(id = 6L, unreadCount = 1, lastReadMessageId = 0L, rating = 0),
        ),
    )

    @Before
    fun setUp() {
        every { getNewsPaging() } returns flowOf(PagingData.empty())
        every { getUnreadChannelsFlow() } returns unreadChannels
        coEvery { getCurrentUser() } returns UserModel("First", "Last", "")

        viewModel = FeedViewModel(
            useCases = NewsViewModelUseCases(
                getCurrentUser = getCurrentUser,
                getNewsPaging = getNewsPaging,
                updateChannelRating = updateChannelRating,
                downloadMediaAndGetPath = downloadMediaAndGetPath,
                markMessageAsRead = markMessageAsRead,
                getReadablePostTime = getReadablePostTime,
                getUnreadChannelsFlow = getUnreadChannelsFlow,
            ),
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `starred channels derived from unread flow`() {
        assertEquals(listOf(5L), viewModel.starredChannels.value)
    }

    @Test
    fun `like event adds rating when not liked before`() {
        viewModel.onEvent(NewsUiEvent.Like(channelId = 1L, isLiked = null))

        coVerify { updateChannelRating(1L, 1) }
    }

    @Test
    fun `like event removes rating when already liked`() {
        viewModel.onEvent(NewsUiEvent.Like(channelId = 1L, isLiked = true))

        coVerify { updateChannelRating(1L, -1) }
    }

    @Test
    fun `dislike event swaps like for dislike`() {
        viewModel.onEvent(NewsUiEvent.Dislike(channelId = 1L, isLiked = true))

        coVerify { updateChannelRating(1L, -2) }
    }

    @Test
    fun `star event removes star and lowers rating when starred`() {
        viewModel.onEvent(NewsUiEvent.StarChannel(channelId = 5L, isStarred = true))

        coVerify { updateChannelRating(5L, -100) }
        assertEquals(emptyList<Long>(), viewModel.starredChannels.value)
    }

    @Test
    fun `star event adds star and raises rating when not starred`() {
        viewModel.onEvent(NewsUiEvent.StarChannel(channelId = 6L, isStarred = false))

        coVerify { updateChannelRating(6L, 100) }
        assertEquals(listOf(5L, 6L), viewModel.starredChannels.value)
    }

    @Test
    fun `mark as read fires only once per message`() {
        viewModel.onEvent(NewsUiEvent.MarkAsRead(messageId = 10L, channelId = 5L))
        viewModel.onEvent(NewsUiEvent.MarkAsRead(messageId = 10L, channelId = 5L))

        coVerify(exactly = 1) { markMessageAsRead(10L, 5L) }
    }
}
