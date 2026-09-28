package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.repository.ChannelRepository
import javax.inject.Inject

class AdjustChannelWeightUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
) {
    suspend operator fun invoke(id: Long, delta: Int) {
        channelRepository.updateChannelRating(id, delta)
    }
}
