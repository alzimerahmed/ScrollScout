package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ReorderChannelUseCaseTest {

    private val repository: ChannelRepository = mockk(relaxed = true)
    private val useCase = ReorderChannelUseCase(repository)

    @Test
    fun `invoke delegates order update to repository`() = runTest {
        useCase(id = 3L, order = 5)

        coVerify(exactly = 1) { repository.updateChannelOrder(3L, 5) }
    }
}
