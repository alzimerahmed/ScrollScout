package com.therxmv.dirolreader.data.source.remote.message

import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.ContactModel
import com.therxmv.dirolreader.domain.models.LocationModel
import com.therxmv.dirolreader.domain.models.PollModel
import com.therxmv.dirolreader.domain.models.PollOptionModel
import org.drinkless.tdlib.TdApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Covers the extended message-type mapping (G12). Only attachment branches are
 * exercised — text/caption branches call `Client.execute` which needs the
 * native tdlib library and cannot run in JVM unit tests.
 */
class MessagesTypesTest {

    private val channel = ChannelData(id = 5L, rating = 0, name = "channel")

    private fun message(content: TdApi.MessageContent) = TdApi.Message().apply {
        id = 10L
        date = 100
        this.content = content
    }

    private fun formattedText(text: String) = TdApi.FormattedText(text, emptyArray())

    @Test
    fun `poll maps to attachment and degrades to question summary text`() {
        val poll = TdApi.Poll().apply {
            id = 7L
            question = formattedText("Best feature?")
            options = arrayOf(
                TdApi.PollOption(formattedText("Polls"), 6, 60, true, false),
                TdApi.PollOption(formattedText("Threads"), 4, 40, false, false),
            )
            totalVoterCount = 10
            isAnonymous = true
            type = TdApi.PollTypeRegular(false)
            isClosed = false
        }

        val model = handleMessageType(channel, message(TdApi.MessagePoll(poll)))

        assertEquals("Poll: Best feature?", model.text)
        assertEquals(
            PollModel(
                id = 7L,
                question = "Best feature?",
                options = listOf(
                    PollOptionModel(text = "Polls", voterCount = 6, votePercentage = 60, isChosen = true),
                    PollOptionModel(text = "Threads", voterCount = 4, votePercentage = 40, isChosen = false),
                ),
                totalVoterCount = 10,
                isAnonymous = true,
                isClosed = false,
                isQuiz = false,
                allowMultipleAnswers = false,
                correctOptionId = -1,
            ),
            model.attachment,
        )
    }

    @Test
    fun `quiz poll maps type flags`() {
        val poll = TdApi.Poll().apply {
            question = formattedText("2 + 2?")
            options = arrayOf(TdApi.PollOption(formattedText("4"), 0, 0, false, false))
            type = TdApi.PollTypeQuiz(0, formattedText("basic math"))
        }

        val model = handleMessageType(channel, message(TdApi.MessagePoll(poll)))

        assertEquals(
            PollModel(
                id = 0L,
                question = "2 + 2?",
                options = listOf(
                    PollOptionModel(text = "4", voterCount = 0, votePercentage = 0, isChosen = false),
                ),
                totalVoterCount = 0,
                isAnonymous = false,
                isClosed = false,
                isQuiz = true,
                allowMultipleAnswers = false,
                correctOptionId = 0,
            ),
            model.attachment,
        )
    }

    @Test
    fun `venue maps coordinates and title with summary text`() {
        val venue = TdApi.Venue(
            TdApi.Location(40.7128, -74.006, 0.0),
            "Central Park",
            "New York, NY",
            "foursquare",
            "venue-id",
            "park",
        )

        val model = handleMessageType(channel, message(TdApi.MessageVenue(venue)))

        assertEquals("Venue: Central Park — New York, NY", model.text)
        assertEquals(
            LocationModel(
                latitude = 40.7128,
                longitude = -74.006,
                title = "Central Park",
                address = "New York, NY",
                isLive = false,
            ),
            model.attachment,
        )
    }

    @Test
    fun `live location maps isLive flag and coordinates summary`() {
        val content = TdApi.MessageLocation(
            TdApi.Location(51.5074, -0.1278, 10.0),
            60,
            30,
            0,
            0,
        )

        val model = handleMessageType(channel, message(content))

        assertEquals("Live location: 51.5074, -0.1278", model.text)
        assertEquals(
            LocationModel(
                latitude = 51.5074,
                longitude = -0.1278,
                title = null,
                address = null,
                isLive = true,
            ),
            model.attachment,
        )
    }

    @Test
    fun `contact maps name and phone with summary text`() {
        val contact = TdApi.Contact(
            "+1 555 0100",
            "Jane",
            "Doe",
            "",
            42L,
        )

        val model = handleMessageType(channel, message(TdApi.MessageContact(contact)))

        assertEquals("Contact: Jane Doe +1 555 0100", model.text)
        assertEquals(
            ContactModel(
                firstName = "Jane",
                lastName = "Doe",
                phoneNumber = "+1 555 0100",
                userId = 42L,
            ),
            model.attachment,
        )
    }

    @Test
    fun `contact without name falls back to phone number`() {
        val contact = TdApi.Contact("+1 555 0100", "", "", "", 0L)

        val model = handleMessageType(channel, message(TdApi.MessageContact(contact)))

        assertEquals("Contact: +1 555 0100", model.text)
    }

    @Test
    fun `unsupported content keeps fallback text and no attachment`() {
        val model = handleMessageType(channel, message(TdApi.MessageUnsupported()))

        assertEquals("This message type is not yet supported", model.text)
        assertNull(model.attachment)
    }
}
