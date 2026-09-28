package com.therxmv.dirolreader.domain.usecase.channel

import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
) {
    fun observe(): Flow<List<ChannelModel>> = channelRepository.getChannelsFlow()

    suspend fun refresh(): List<ChannelModel> = channelRepository.refreshChannels()
}
