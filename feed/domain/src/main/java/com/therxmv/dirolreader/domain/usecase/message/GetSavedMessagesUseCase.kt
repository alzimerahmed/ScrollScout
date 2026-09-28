package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.repository.SavedMessagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedMessagesUseCase @Inject constructor(
    private val savedMessagesRepository: SavedMessagesRepository,
) {

    operator fun invoke(): Flow<List<MessageModel>> =
        savedMessagesRepository.getSavedMessages()
}
