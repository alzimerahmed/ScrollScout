package com.therxmv.dirolreader.domain.models

/**
 * Structured payload of a non-media message (poll, location/venue, contact).
 * Attached to [MessageModel.attachment]; rendered by dedicated composables
 * in feed:presentation instead of plain markdown text.
 */
sealed interface MessageAttachment {

    /**
     * Plain-text summary used as [MessageModel.text] so cached, saved and
     * searched posts degrade to readable text when the attachment itself is
     * not persisted (offline fallback, search results, saved view).
     */
    fun summaryText(): String
}

data class PollModel(
    val id: Long,
    val question: String,
    val options: List<PollOptionModel>,
    val totalVoterCount: Int,
    val isAnonymous: Boolean,
    val isClosed: Boolean,
    val isQuiz: Boolean,
    val allowMultipleAnswers: Boolean,
    val correctOptionId: Int,
) : MessageAttachment {

    val hasVoted: Boolean
        get() = options.any { it.isChosen }

    override fun summaryText() = "Poll: $question"
}

data class PollOptionModel(
    val text: String,
    val voterCount: Int,
    val votePercentage: Int,
    val isChosen: Boolean,
)

data class LocationModel(
    val latitude: Double,
    val longitude: Double,
    val title: String?,
    val address: String?,
    val isLive: Boolean,
) : MessageAttachment {

    val geoUri: String
        get() = "geo:$latitude,$longitude"

    override fun summaryText() = when {
        title != null -> listOfNotNull(title, address)
            .joinToString(" — ")
            .let { "Venue: $it" }

        isLive -> "Live location: $latitude, $longitude"
        else -> "Location: $latitude, $longitude"
    }
}

data class ContactModel(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val userId: Long,
) : MessageAttachment {

    val displayName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { phoneNumber }

    override fun summaryText() = if (displayName == phoneNumber) {
        "Contact: $phoneNumber"
    } else {
        "Contact: $displayName $phoneNumber"
    }
}
