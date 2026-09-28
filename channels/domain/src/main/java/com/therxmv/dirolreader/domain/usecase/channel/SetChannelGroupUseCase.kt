package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import javax.inject.Inject

class SetChannelGroupUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
) {
    suspend operator fun invoke(id: Long, group: String) {
        channelRepository.updateChannelGroup(id, group.trim())
    }
}
