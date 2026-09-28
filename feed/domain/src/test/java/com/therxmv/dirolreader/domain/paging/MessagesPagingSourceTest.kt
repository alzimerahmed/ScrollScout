package com.therxmv.dirolreader.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.therxmv.common.Paging.PAGE_SIZE
import com.therxmv.common.Paging.STARTING_PAGE_INDEX
import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesPagingSourceTest {

    private val repository: MessageRepository = mockk()
    private lateinit var pagingSource: MessagesPagingSource

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
        pagingSource = MessagesPagingSource(
            messageRepository = repository,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load returns page with next key when data is not empty`() = runTest {
        val data = List(PAGE_SIZE) { message(it.toLong()) }
        coEvery { repository.getUnreadMessagesByPage(STARTING_PAGE_INDEX) } returns data

        val result = pagingSource.load(PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = PAGE_SIZE,
            placeholdersEnabled = false,
        ))

        val page = result as PagingSource.LoadResult.Page
        assertEquals(data, page.data)
        assertEquals(null, page.prevKey)
        assertEquals(STARTING_PAGE_INDEX + 1, page.nextKey)
    }

    @Test
    fun `load returns no next key when data is empty`() = runTest {
        coEvery { repository.getUnreadMessagesByPage(STARTING_PAGE_INDEX) } returns emptyList()

        val result = pagingSource.load(PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = PAGE_SIZE,
            placeholdersEnabled = false,
        ))

        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertEquals(null, page.nextKey)
    }

    @Test
    fun `load returns error result when repository throws`() = runTest {
        coEvery { repository.getUnreadMessagesByPage(STARTING_PAGE_INDEX) } throws RuntimeException("boom")

        val result = pagingSource.load(PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = PAGE_SIZE,
            placeholdersEnabled = false,
        ))

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `refresh key derived from anchor position`() {
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = List(PAGE_SIZE) { message(it.toLong()) },
                    prevKey = null,
                    nextKey = 1,
                ),
            ),
            anchorPosition = 5,
            config = androidx.paging.PagingConfig(PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(1, refreshKey)
    }
}
