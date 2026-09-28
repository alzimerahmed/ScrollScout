package com.therxmv.dirolreader.data.repository

import com.therxmv.common.Paging.STARTING_PAGE_INDEX
import com.therxmv.dirolreader.data.source.local.db.CachedMessageLocalDataSource
import com.therxmv.dirolreader.data.source.remote.media.MediaSource
import com.therxmv.dirolreader.data.source.remote.message.MessageSource
import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.MessageModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MessageRepositoryImplTest {

    private val messageRemoteDataSource: MessageSource = mockk()
    private val mediaRemoteDataSource: MediaSource = mockk()
    private val cachedMessageLocalDataSource: CachedMessageLocalDataSource = mockk(relaxed = true)
    private lateinit var repository: MessageRepositoryImpl

    private fun message(id: Long) = MessageModel(
        id = id,
        channelData = ChannelData(id = 1L, rating = 0, name = "channel"),
        timestamp = 0,
        text = "text",
        mediaList = null,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = MessageRepositoryImpl(
            messageRemoteDataSource = messageRemoteDataSource,
            mediaRemoteDataSource = mediaRemoteDataSource,
            cachedMessageLocalDataSource = cachedMessageLocalDataSource,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetched page is cached write-through`() = runTest {
        val page = listOf(message(1L), message(2L))
        coEvery { messageRemoteDataSource.getUnreadMessagesByPage(STARTING_PAGE_INDEX) } returns page

        assertEquals(page, repository.getUnreadMessagesByPage(STARTING_PAGE_INDEX))
        coVerify(exactly = 1) { cachedMessageLocalDataSource.saveMessages(page) }
    }

    @Test
    fun `remote failure on first page falls back to cached snapshot`() = runTest {
        val cached = listOf(message(3L))
        coEvery { messageRemoteDataSource.getUnreadMessagesByPage(STARTING_PAGE_INDEX) } throws RuntimeException("offline")
        coEvery { cachedMessageLocalDataSource.getCachedMessages(any()) } returns cached

        assertEquals(cached, repository.getUnreadMessagesByPage(STARTING_PAGE_INDEX))
    }

    @Test
    fun `remote failure on later page returns empty list`() = runTest {
        coEvery { messageRemoteDataSource.getUnreadMessagesByPage(1) } throws RuntimeException("offline")

        assertEquals(emptyList<MessageModel>(), repository.getUnreadMessagesByPage(1))
        coVerify(exactly = 0) { cachedMessageLocalDataSource.getCachedMessages(any()) }
    }

    @Test
    fun `search delegates to local cache data source`() = runTest {
        val results = listOf(message(5L))
        coEvery { cachedMessageLocalDataSource.searchMessages("query") } returns results

        assertEquals(results, repository.searchMessages("query"))
    }
}
