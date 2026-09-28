package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.repository.SavedMessagesRepository
import javax.inject.Inject

class SaveMessageUseCase @Inject constructor(
    private val savedMessagesRepository: SavedMessagesRepository,
) {

    suspend operator fun invoke(message: MessageModel) =
        savedMessagesRepository.saveMessage(message)
}
