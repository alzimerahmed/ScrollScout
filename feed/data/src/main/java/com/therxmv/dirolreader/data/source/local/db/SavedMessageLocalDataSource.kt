package com.therxmv.dirolreader.data.source.local.db

import com.therxmv.dirolreader.data.entity.SavedMessageEntity
import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.MessageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class SavedMessageLocalDataSource @Inject constructor(
    private val dirolDao: DirolDao,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun saveMessage(message: MessageModel) = withContext(ioDispatcher) {
        dirolDao.insertSavedMessage(message.toEntity())
    }

    suspend fun removeMessage(messageId: Long) = withContext(ioDispatcher) {
        dirolDao.deleteSavedMessage(messageId)
    }

    fun getSavedMessages(): Flow<List<MessageModel>> =
        dirolDao.getSavedMessagesFlow().map { list -> list.map { it.toDomain() } }
}

fun MessageModel.toEntity() = SavedMessageEntity(
    messageId = id,
    channelId = channelData.id,
    channelName = channelData.name,
    text = text,
    timestamp = timestamp,
    savedAt = System.currentTimeMillis(),
)

fun SavedMessageEntity.toDomain() = MessageModel(
    id = messageId,
    channelData = ChannelData(
        id = channelId,
        rating = 0,
        name = channelName,
    ),
    timestamp = timestamp,
    text = text,
    mediaList = null,
)
