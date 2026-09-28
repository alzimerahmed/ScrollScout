package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.repository.ChannelRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetChannelsUseCaseTest {

    private val repository: ChannelRepository = mockk(relaxed = true)
    private val useCase = GetChannelsUseCase(repository)

    @Test
    fun `observe returns repository flow`() = runTest {
        val expected = listOf(ChannelModel(id = 1L, unreadCount = 0, lastReadMessageId = 0L))
        every { repository.getChannelsFlow() } returns flowOf(expected)

        val result = useCase.observe().first()

        assertEquals(expected, result)
    }

    @Test
    fun `refresh delegates to repository`() = runTest {
        val expected = listOf(ChannelModel(id = 2L, unreadCount = 1, lastReadMessageId = 5L))
        coEvery { repository.refreshChannels() } returns expected

        val result = useCase.refresh()

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.refreshChannels() }
    }
}
