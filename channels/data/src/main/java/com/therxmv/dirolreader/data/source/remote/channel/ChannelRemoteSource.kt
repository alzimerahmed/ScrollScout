package com.therxmv.dirolreader.data.source.remote.channel

import com.therxmv.dirolreader.data.entity.ChannelEntity

interface ChannelRemoteSource {
    suspend fun getChannelsForPaging(): List<ChannelEntity>
    suspend fun getAllChannels(): List<ChannelEntity>
    suspend fun setChannelMuted(chatId: Long, isMuted: Boolean): Boolean
}
