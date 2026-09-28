package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetChannelGroupUseCaseTest {

    private val repository: ChannelRepository = mockk(relaxed = true)
    private val useCase = SetChannelGroupUseCase(repository)

    @Test
    fun `invoke trims group name before delegating`() = runTest {
        useCase(id = 1L, group = "  news  ")

        coVerify(exactly = 1) { repository.updateChannelGroup(1L, "news") }
    }
}
