package com.therxmv.dirolreader.data.source.local.db

import com.therxmv.dirolreader.data.entity.ChannelEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class ChannelLocalDataSource @Inject constructor(
    private val dirolDao: DirolDao,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getAllChannels(): List<ChannelEntity> = withContext(ioDispatcher) {
        dirolDao.getAllChannels()
    }

    fun getAllChannelsFlow(): Flow<List<ChannelEntity>> =
        dirolDao.getAllChannelsFlow().flowOn(ioDispatcher)

    suspend fun updateChannelRating(id: Long, num: Int) = withContext(ioDispatcher) {
        dirolDao.updateChannelRating(id, num)
    }

    suspend fun updateChannelMuted(id: Long, isMuted: Boolean) = withContext(ioDispatcher) {
        dirolDao.updateChannelMuted(id, isMuted)
    }

    suspend fun updateChannelOrder(id: Long, order: Int) = withContext(ioDispatcher) {
        dirolDao.updateChannelOrder(id, order)
    }

    suspend fun updateChannelGroup(id: Long, group: String) = withContext(ioDispatcher) {
        dirolDao.updateChannelGroup(id, group)
    }

    suspend fun resetChannelRatings() = withContext(ioDispatcher) {
        dirolDao.resetChannelRatings()
    }

    suspend fun addChannels(channelEntity: List<ChannelEntity>) = withContext(ioDispatcher) {
        channelEntity.map {
            dirolDao.insertOrUpdateChannel(it)
        }.size
    }
}
