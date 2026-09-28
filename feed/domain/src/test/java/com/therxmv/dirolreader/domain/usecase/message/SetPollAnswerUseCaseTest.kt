package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.models.PollModel
import com.therxmv.dirolreader.domain.models.PollOptionModel
import com.therxmv.dirolreader.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SetPollAnswerUseCaseTest {

    private val repository: MessageRepository = mockk()
    private val useCase = SetPollAnswerUseCase(repository)

    @Test
    fun `vote delegates to repository and returns refreshed poll`() = runTest {
        val updatedPoll = poll(isChosen = true)
        coEvery { repository.setPollAnswer(5L, 10L, intArrayOf(0)) } returns updatedPoll

        assertEquals(updatedPoll, useCase(5L, 10L, intArrayOf(0)))
    }

    @Test
    fun `rejected vote returns null`() = runTest {
        coEvery { repository.setPollAnswer(5L, 10L, intArrayOf(1)) } returns null

        assertNull(useCase(5L, 10L, intArrayOf(1)))
    }

    @Test
    fun `empty option list retracts the answer`() = runTest {
        coEvery { repository.setPollAnswer(5L, 10L, intArrayOf()) } returns poll(isChosen = false)

        useCase(5L, 10L, intArrayOf())

        coVerify(exactly = 1) { repository.setPollAnswer(5L, 10L, intArrayOf()) }
    }

    private fun poll(isChosen: Boolean) = PollModel(
        id = 1L,
        question = "Question",
        options = listOf(
            PollOptionModel(text = "A", voterCount = 1, votePercentage = 100, isChosen = isChosen),
        ),
        totalVoterCount = 1,
        isAnonymous = true,
        isClosed = false,
        isQuiz = false,
        allowMultipleAnswers = false,
        correctOptionId = -1,
    )
}
