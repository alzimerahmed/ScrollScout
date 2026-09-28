package com.therxmv.dirolreader.data.source.local.db

import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.models.PollModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
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
class CachedMessageLocalDataSourceTest {

    private val dao: CachedMessageDao = mockk(relaxed = true)
    private lateinit var dataSource: CachedMessageLocalDataSource

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        dataSource = CachedMessageLocalDataSource(
            cachedMessageDao = dao,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search quotes every token literally`() = runTest {
        coEvery { dao.searchCachedMessages(any()) } returns emptyList()

        dataSource.searchMessages("a\"b OR c: (d)")

        coVerify(exactly = 1) { dao.searchCachedMessages("\"ab\" \"OR\" \"c:\" \"(d)\"") }
    }

    @Test
    fun `whitespace-only query does not hit the dao`() = runTest {
        assertEquals(emptyList<MessageModel>(), dataSource.searchMessages(" \t "))

        coVerify(exactly = 0) { dao.searchCachedMessages(any()) }
    }

    @Test
    fun `saved messages are keyed per channel`() = runTest {
        dataSource.saveMessages(listOf(message(id = 7L, channelId = 1L), message(id = 7L, channelId = 2L)))

        coVerify(exactly = 2) { dao.insertCachedMessage(any()) }
    }

    @Test
    fun `attachment posts are snapshotted with their summary text`() = runTest {
        val pollPost = message(id = 9L, channelId = 1L).copy(
            text = "Poll: Best feature?",
            attachment = PollModel(
                id = 3L,
                question = "Best feature?",
                options = emptyList(),
                totalVoterCount = 0,
                isAnonymous = true,
                isClosed = false,
                isQuiz = false,
                allowMultipleAnswers = false,
                correctOptionId = -1,
            ),
        )

        dataSource.saveMessages(listOf(pollPost))

        verify { dao.insertCachedMessage(match { it.text == "Poll: Best feature?" }) }
    }

    private fun message(id: Long, channelId: Long) = MessageModel(
        id = id,
        channelData = ChannelData(id = channelId, rating = 0, name = "channel"),
        timestamp = 0,
        text = "text",
        mediaList = null,
    )
}
