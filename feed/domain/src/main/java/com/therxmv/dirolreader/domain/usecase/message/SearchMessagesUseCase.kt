package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class SearchMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(query: String): List<MessageModel> = withContext(ioDispatcher) {
        if (query.isBlank()) {
            emptyList()
        } else {
            messageRepository.searchMessages(query)
        }
    }
}
