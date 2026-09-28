package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchMessagesUseCaseTest {

    private val repository: MessageRepository = mockk()
    private val useCase = SearchMessagesUseCase(repository)

    private fun message(id: Long) = MessageModel(
        id = id,
        channelData = ChannelData(id = 1L, rating = 0, name = "channel"),
        timestamp = 0,
        text = "text",
        mediaList = null,
    )

    @Test
    fun `blank query returns empty list without hitting repository`() = runTest {
        assertEquals(emptyList<MessageModel>(), useCase("   "))
        coVerify(exactly = 0) { repository.searchMessages(any()) }
    }

    @Test
    fun `query delegates to repository search`() = runTest {
        val expected = listOf(message(1L), message(2L))
        coEvery { repository.searchMessages("kotlin") } returns expected

        assertEquals(expected, useCase("kotlin"))
    }

    @Test
    fun `repository failure propagates to caller`() = runTest {
        coEvery { repository.searchMessages("kotlin") } throws RuntimeException("db closed")

        var thrown: Throwable? = null
        try {
            useCase("kotlin")
        } catch (e: RuntimeException) {
            thrown = e
        }

        assertEquals("db closed", thrown?.message)
    }
}
