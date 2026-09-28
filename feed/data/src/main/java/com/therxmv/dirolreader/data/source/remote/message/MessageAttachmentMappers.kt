package com.therxmv.dirolreader.data.source.remote.message

import com.therxmv.dirolreader.domain.models.ContactModel
import com.therxmv.dirolreader.domain.models.LocationModel
import com.therxmv.dirolreader.domain.models.MessageAttachment
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.models.PollModel
import com.therxmv.dirolreader.domain.models.PollOptionModel
import org.drinkless.tdlib.TdApi
import org.drinkless.tdlib.TdApi.Message

/**
 * Maps extended message types to a UI attachment, or null for content we still
 * can't render. Attachment posts carry a plain-text summary in
 * [com.therxmv.dirolreader.domain.models.MessageModel.text] so search and the
 * offline cache degrade gracefully.
 */
fun Message.attachmentModel(): MessageAttachment? = when (val content = content) {
    is TdApi.MessagePoll -> content.poll.toPollModel()
    is TdApi.MessageLocation -> content.toLocationModel()
    is TdApi.MessageVenue -> content.toLocationModel()
    is TdApi.MessageContact -> content.toContactModel()
    else -> null
}

/** Replaces the fallback text with the attachment summary when supported. */
fun MessageModel.withAttachment(attachment: MessageAttachment?) = attachment?.let {
    copy(
        text = it.summaryText(),
        attachment = it,
    )
} ?: this

internal const val UNKNOWN_CORRECT_OPTION = -1

fun TdApi.Poll.toPollModel() = PollModel(
    id = id,
    question = question.text,
    options = options.map { it.toPollOptionModel() },
    totalVoterCount = totalVoterCount,
    isAnonymous = isAnonymous,
    isClosed = isClosed,
    isQuiz = type is TdApi.PollTypeQuiz,
    allowMultipleAnswers = (type as? TdApi.PollTypeRegular)?.allowMultipleAnswers == true,
    correctOptionId = (type as? TdApi.PollTypeQuiz)?.correctOptionId ?: UNKNOWN_CORRECT_OPTION,
)

private fun TdApi.PollOption.toPollOptionModel() = PollOptionModel(
    text = text.text,
    voterCount = voterCount,
    votePercentage = votePercentage,
    isChosen = isChosen,
)

fun TdApi.MessageLocation.toLocationModel() = LocationModel(
    latitude = location.latitude,
    longitude = location.longitude,
    title = null,
    address = null,
    isLive = livePeriod > 0,
)

fun TdApi.MessageVenue.toLocationModel() = LocationModel(
    latitude = venue.location.latitude,
    longitude = venue.location.longitude,
    title = venue.title.takeIf { it.isNotBlank() },
    address = venue.address.takeIf { it.isNotBlank() },
    isLive = false,
)

fun TdApi.MessageContact.toContactModel() = ContactModel(
    firstName = contact.firstName,
    lastName = contact.lastName,
    phoneNumber = contact.phoneNumber,
    userId = contact.userId,
)
