package com.therxmv.dirolreader.domain.repository

import com.therxmv.dirolreader.domain.models.ChannelModel
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    suspend fun updateChannelRating(id: Long, num: Int)
    suspend fun refreshChannels(): List<ChannelModel>
    fun getChannelsFlow(): Flow<List<ChannelModel>>
    suspend fun setChannelMuted(id: Long, isMuted: Boolean): Boolean
    suspend fun updateChannelOrder(id: Long, order: Int)
    suspend fun updateChannelGroup(id: Long, group: String)
    suspend fun resetChannelWeights()
}
