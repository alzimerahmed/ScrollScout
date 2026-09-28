package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateChannelRatingUseCaseTest {

    private val repository: ChannelRepository = mockk(relaxed = true)
    private val useCase = UpdateChannelRatingUseCase(repository)

    @Test
    fun `invoke delegates rating update to repository`() = runTest {
        useCase(id = 42L, num = 100)

        coVerify(exactly = 1) { repository.updateChannelRating(42L, 100) }
    }
}
