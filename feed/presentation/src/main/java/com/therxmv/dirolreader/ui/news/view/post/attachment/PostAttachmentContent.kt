package com.therxmv.dirolreader.ui.news.view.post.attachment

import androidx.compose.runtime.Composable
import com.therxmv.dirolreader.domain.models.ContactModel
import com.therxmv.dirolreader.domain.models.LocationModel
import com.therxmv.dirolreader.domain.models.MessageAttachment
import com.therxmv.dirolreader.domain.models.PollModel

/**
 * Sends a poll vote through the client wrapper; returns the refreshed poll
 * state, or null when the vote failed. Mirrors [MediaLoaderType].
 */
typealias PollVoterType = suspend (chatId: Long, messageId: Long, optionIds: IntArray) -> PollModel?

@Composable
fun PostAttachmentContent(
    attachment: MessageAttachment,
    channelId: Long,
    messageId: Long,
    votePoll: PollVoterType,
) {
    when (attachment) {
        is PollModel -> PostPoll(
            poll = attachment,
            onVote = { optionIds -> votePoll(channelId, messageId, optionIds) },
        )

        is LocationModel -> PostLocation(location = attachment)

        is ContactModel -> PostContact(contact = attachment)
    }
}
