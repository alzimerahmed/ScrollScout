package com.therxmv.dirolreader.ui.news.viewmodel

import androidx.paging.PagingData
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.models.UserModel
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
import com.therxmv.dirolreader.domain.usecase.message.TranslateTextUseCase
import com.therxmv.dirolreader.domain.usecase.user.GetCurrentUserUseCase
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent
import com.therxmv.dirolreader.ui.news.viewmodel.utils.TranslationState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
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
    private val saveMessage: SaveMessageUseCase = mockk(relaxed = true)
    private val getSavedMessages: GetSavedMessagesUseCase = mockk()
    private val markAllAsRead: MarkAllAsReadUseCase = mockk(relaxed = true)
    private val translateText: TranslateTextUseCase = mockk()

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
        every { getSavedMessages() } returns emptyFlow()
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
                saveMessage = saveMessage,
                getSavedMessages = getSavedMessages,
                markAllAsRead = markAllAsRead,
                translateText = translateText,
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

    @Test
    fun `save event persists message snapshot`() {
        viewModel.onEvent(
            NewsUiEvent.SaveMessage(
                messageId = 10L,
                channelId = 5L,
                channelName = "Channel",
                text = "Post text",
            ),
        )

        coVerify {
            saveMessage(
                MessageModel(
                    id = 10L,
                    channelData = match { it.id == 5L && it.name == "Channel" },
                    timestamp = 0,
                    text = "Post text",
                    mediaList = null,
                ),
            )
        }
    }

    @Test
    fun `mark all as read delegates to use case`() {
        viewModel.onEvent(NewsUiEvent.MarkAllAsRead(messageIds = listOf(10L, 11L)))

        coVerify(exactly = 1) { markAllAsRead() }
    }

    @Test
    fun `saved view toggles on and off`() {
        assertEquals(false, viewModel.isSavedView.value)

        viewModel.onEvent(NewsUiEvent.ToggleSavedView)
        assertEquals(true, viewModel.isSavedView.value)

        viewModel.onEvent(NewsUiEvent.ToggleSavedView)
        assertEquals(false, viewModel.isSavedView.value)
    }

    @Test
    fun `saved messages collected from use case flow`() {
        val savedFlow = MutableStateFlow(listOf(savedMessage(10L)))
        every { getSavedMessages() } returns savedFlow
        viewModel = buildViewModel()

        assertEquals(listOf(savedMessage(10L)), viewModel.savedMessages.value)
    }

    @Test
    fun `translate event exposes ready state on success`() {
        coEvery { translateText("hello") } returns "hola"

        viewModel.onEvent(NewsUiEvent.Translate(text = "hello"))

        assertEquals(TranslationState.Ready("hola"), viewModel.translationState.value)
    }

    @Test
    fun `translate event exposes error state on failure`() {
        coEvery { translateText("hello") } throws RuntimeException("offline")

        viewModel.onEvent(NewsUiEvent.Translate(text = "hello"))

        assertEquals(TranslationState.Error, viewModel.translationState.value)
    }

    @Test
    fun `blank text is not translated`() {
        viewModel.onEvent(NewsUiEvent.Translate(text = "   "))

        coVerify(exactly = 0) { translateText(any()) }
        assertEquals(TranslationState.Idle, viewModel.translationState.value)
    }

    @Test
    fun `dismiss translation resets state to idle`() {
        coEvery { translateText("hello") } returns "hola"
        viewModel.onEvent(NewsUiEvent.Translate(text = "hello"))

        viewModel.onEvent(NewsUiEvent.DismissTranslation)

        assertEquals(TranslationState.Idle, viewModel.translationState.value)
    }

    private fun savedMessage(id: Long) = MessageModel(
        id = id,
        channelData = com.therxmv.dirolreader.domain.models.ChannelData(
            id = 5L,
            rating = 0,
            name = "Channel",
        ),
        timestamp = 0,
        text = "Saved post",
        mediaList = null,
    )

    private fun buildViewModel() = FeedViewModel(
        useCases = NewsViewModelUseCases(
            getCurrentUser = getCurrentUser,
            getNewsPaging = getNewsPaging,
            updateChannelRating = updateChannelRating,
            downloadMediaAndGetPath = downloadMediaAndGetPath,
            markMessageAsRead = markMessageAsRead,
            getReadablePostTime = getReadablePostTime,
            getUnreadChannelsFlow = getUnreadChannelsFlow,
            saveMessage = saveMessage,
            getSavedMessages = getSavedMessages,
            markAllAsRead = markAllAsRead,
            translateText = translateText,
        ),
        ioDispatcher = UnconfinedTestDispatcher(),
    )
}
