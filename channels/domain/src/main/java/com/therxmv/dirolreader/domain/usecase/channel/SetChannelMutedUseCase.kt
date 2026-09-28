package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import javax.inject.Inject

class SetChannelMutedUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
) {
    suspend operator fun invoke(id: Long, isMuted: Boolean): Boolean =
        channelRepository.setChannelMuted(id, isMuted)
}
