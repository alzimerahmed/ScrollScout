package com.therxmv.dirolreader.data.repository

import com.therxmv.dirolreader.data.source.local.db.ChannelLocalDataSource
import com.therxmv.dirolreader.data.source.remote.channel.ChannelRemoteSource
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.repository.ChannelRepository
import com.therxmv.dirolreader.data.entity.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val channelLocalDataSource: ChannelLocalDataSource,
    private val channelRemoteSource: ChannelRemoteSource,
) : ChannelRepository {

    override suspend fun updateChannelRating(id: Long, num: Int) {
        channelLocalDataSource.updateChannelRating(id, num)
    }

    override suspend fun refreshChannels(): List<ChannelModel> =
        channelRemoteSource.getAllChannels().map { it.toDomain() }

    override fun getChannelsFlow(): Flow<List<ChannelModel>> =
        channelLocalDataSource.getAllChannelsFlow().map { channels ->
            channels.map { it.toDomain() }
        }

    override suspend fun setChannelMuted(id: Long, isMuted: Boolean): Boolean {
        val success = channelRemoteSource.setChannelMuted(id, isMuted)
        if (success) {
            channelLocalDataSource.updateChannelMuted(id, isMuted)
        }
        return success
    }

    override suspend fun updateChannelOrder(id: Long, order: Int) {
        channelLocalDataSource.updateChannelOrder(id, order)
    }

    override suspend fun updateChannelGroup(id: Long, group: String) {
        channelLocalDataSource.updateChannelGroup(id, group)
    }

    override suspend fun resetChannelWeights() {
        channelLocalDataSource.resetChannelRatings()
    }
}
