package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.repository.MessageRepository
import javax.inject.Inject

class SearchMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
) {
    suspend operator fun invoke(query: String): List<MessageModel> {
        if (query.isBlank()) return emptyList()
        return messageRepository.searchMessages(query)
    }
}
