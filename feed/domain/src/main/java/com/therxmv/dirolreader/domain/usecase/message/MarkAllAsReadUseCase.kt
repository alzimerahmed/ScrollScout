package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.repository.MessageRepository
import javax.inject.Inject

class MarkAllAsReadUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
) {

    suspend operator fun invoke() = messageRepository.markAllAsRead()
}
