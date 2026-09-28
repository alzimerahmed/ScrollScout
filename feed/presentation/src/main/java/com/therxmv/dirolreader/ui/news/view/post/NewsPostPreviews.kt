package com.therxmv.dirolreader.ui.news.view.post

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.therxmv.dirolreader.domain.models.ContactModel
import com.therxmv.dirolreader.domain.models.LocationModel
import com.therxmv.dirolreader.domain.models.MediaModel
import com.therxmv.dirolreader.domain.models.MediaType
import com.therxmv.dirolreader.domain.models.PollModel
import com.therxmv.dirolreader.domain.models.PollOptionModel
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
private fun PostWithTextPreview() {
    NewsPost(
        data = NewsPostUiData(
            id = 0,
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            mediaList = null,
            channelData = getChannelData(),
        ),
        handlers = postHandlers(),
        isStarred = false,
        actions = postActions(),
    )
}

@Preview
@Composable
private fun PostWithTextAndOnePhotoPreview() {
    NewsPost(
        data = NewsPostUiData(
            id = 0,
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            mediaList = persistentListOf(
                getMediaModel(),
            ),
            channelData = getChannelData(),
        ),
        handlers = postHandlers(),
        isStarred = false,
        actions = postActions(),
    )
}

@Preview
@Composable
private fun PostWithoutTextAndOnePhotoPreview() {
    NewsPost(
        data = NewsPostUiData(
            id = 0,
            text = "",
            mediaList = persistentListOf(
                getMediaModel(),
            ),
            channelData = getChannelData(),
        ),
        handlers = postHandlers(),
        isStarred = true,
        actions = postActions(),
    )
}

@Preview
@Composable
private fun PostWithTextAndThreeMediaPreview() {
    NewsPost(
        data = NewsPostUiData(
            id = 0,
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            mediaList = persistentListOf(
                getMediaModel(),
                getMediaModel(),
                getMediaModel(),
            ),
            channelData = getChannelData(),
        ),
        handlers = postHandlers(),
        isStarred = true,
        actions = postActions(),
    )
}

@Preview
@Composable
private fun PostWithPollPreview() {
    NewsPost(
        data = NewsPostUiData(
            id = 0,
            text = "Poll: Which feature should ship next?",
            mediaList = null,
            channelData = getChannelData(),
            attachment = PollModel(
                id = 1L,
                question = "Which feature should ship next?",
                options = listOf(
                    PollOptionModel(text = "Polls", voterCount = 6, votePercentage = 60, isChosen = true),
                    PollOptionModel(text = "Threads", voterCount = 3, votePercentage = 30, isChosen = false),
                    PollOptionModel(text = "Stories", voterCount = 1, votePercentage = 10, isChosen = false),
                ),
                totalVoterCount = 10,
                isAnonymous = true,
                isClosed = false,
                isQuiz = false,
                allowMultipleAnswers = false,
                correctOptionId = -1,
            ),
        ),
        handlers = postHandlers(),
        isStarred = false,
        actions = postActions(),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostWithLocationPreview() {
    NewsPost(
        data = NewsPostUiData(
            id = 0,
            text = "Venue: Central Park — New York, NY",
            mediaList = null,
            channelData = getChannelData(),
            attachment = LocationModel(
                latitude = 40.7128,
                longitude = -74.006,
                title = "Central Park",
                address = "New York, NY",
                isLive = false,
            ),
        ),
        handlers = postHandlers(),
        isStarred = false,
        actions = postActions(),
    )
}

@Preview
@Composable
private fun PostWithContactPreview() {
    NewsPost(
        data = NewsPostUiData(
            id = 0,
            text = "Contact: Jane Doe +1 555 0100",
            mediaList = null,
            channelData = getChannelData(),
            attachment = ContactModel(
                firstName = "Jane",
                lastName = "Doe",
                phoneNumber = "+1 555 0100",
                userId = 0L,
            ),
        ),
        handlers = postHandlers(),
        isStarred = false,
        actions = postActions(),
    )
}

private fun getMediaModel() = MediaModel(
    id = 2,
    height = 200,
    width = 200,
    sizeInMb = "200",
    type = MediaType.PHOTO,
)

private fun getChannelData() = ChannelUiData(
    id = 0,
    name = "Channel name",
    avatarPath = "",
    postTime = "2 min ago",
)

private fun postHandlers() = NewsPostHandlers(
    onEvent = {},
    loadMedia = ::loadMessageMedia,
    votePoll = { _, _, _ -> null },
)

private fun postActions() = NewsPostActions(
    onStarChannel = {},
    onLike = {},
    onDislike = {},
    markAsRead = {},
    onTranslate = {},
)

private suspend fun loadMessageMedia(mediaId: Int) = ""
