package com.therxmv.dirolreader.domain.repository

import com.therxmv.dirolreader.domain.models.MessageModel
import kotlinx.coroutines.flow.Flow

interface SavedMessagesRepository {

    suspend fun saveMessage(message: MessageModel)
    suspend fun removeMessage(messageId: Long)
    fun getSavedMessages(): Flow<List<MessageModel>>
}
