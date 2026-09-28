package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AdjustChannelWeightUseCaseTest {

    private val repository: ChannelRepository = mockk(relaxed = true)
    private val useCase = AdjustChannelWeightUseCase(repository)

    @Test
    fun `invoke delegates weight delta to repository`() = runTest {
        useCase(id = 9L, delta = -1)

        coVerify(exactly = 1) { repository.updateChannelRating(9L, -1) }
    }
}
