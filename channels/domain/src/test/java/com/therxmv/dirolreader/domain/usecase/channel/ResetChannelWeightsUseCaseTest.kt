package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ResetChannelWeightsUseCaseTest {

    private val repository: ChannelRepository = mockk(relaxed = true)
    private val useCase = ResetChannelWeightsUseCase(repository)

    @Test
    fun `invoke delegates reset to repository`() = runTest {
        useCase()

        coVerify(exactly = 1) { repository.resetChannelWeights() }
    }
}
