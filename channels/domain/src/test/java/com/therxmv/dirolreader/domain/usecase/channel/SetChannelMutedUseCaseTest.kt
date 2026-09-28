package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetChannelMutedUseCaseTest {

    private val repository: ChannelRepository = mockk(relaxed = true)
    private val useCase = SetChannelMutedUseCase(repository)

    @Test
    fun `invoke delegates mute to repository and returns result`() = runTest {
        useCase(id = 7L, isMuted = true)

        coVerify(exactly = 1) { repository.setChannelMuted(7L, true) }
    }
}
