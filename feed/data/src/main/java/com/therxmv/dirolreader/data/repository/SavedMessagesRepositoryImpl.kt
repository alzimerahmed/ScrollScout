package com.therxmv.dirolreader.data.repository

import com.therxmv.dirolreader.data.source.local.db.SavedMessageLocalDataSource
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.repository.SavedMessagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedMessagesRepositoryImpl @Inject constructor(
    private val savedMessageLocalDataSource: SavedMessageLocalDataSource,
) : SavedMessagesRepository {

    override suspend fun saveMessage(message: MessageModel) =
        savedMessageLocalDataSource.saveMessage(message)

    override suspend fun removeMessage(messageId: Long) =
        savedMessageLocalDataSource.removeMessage(messageId)

    override fun getSavedMessages(): Flow<List<MessageModel>> =
        savedMessageLocalDataSource.getSavedMessages()
}
