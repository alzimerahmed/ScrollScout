package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.models.PollModel
import com.therxmv.dirolreader.domain.repository.MessageRepository
import javax.inject.Inject

/**
 * Sends the user's vote to Telegram via TdLib `SetPollAnswer` and returns the
 * refreshed poll state, or null when the vote was rejected/failed.
 */
class SetPollAnswerUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
) {

    suspend operator fun invoke(
        chatId: Long,
        messageId: Long,
        optionIds: IntArray,
    ): PollModel? = messageRepository.setPollAnswer(chatId, messageId, optionIds)
}
