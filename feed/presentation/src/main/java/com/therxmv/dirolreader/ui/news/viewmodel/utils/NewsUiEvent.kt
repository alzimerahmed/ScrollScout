package com.therxmv.dirolreader.ui.news.viewmodel.utils

sealed class NewsUiEvent {
    data class MarkAsRead(val messageId: Long, val channelId: Long) : NewsUiEvent()
    data class Dismiss(val messageId: Long) : NewsUiEvent()
    data class Like(val channelId: Long, val isLiked: Boolean?) : NewsUiEvent()
    data class Dislike(val channelId: Long, val isLiked: Boolean?) : NewsUiEvent()
    data class StarChannel(val channelId: Long, val isStarred: Boolean) : NewsUiEvent()
    data class SaveMessage(
        val messageId: Long,
        val channelId: Long,
        val channelName: String,
        val text: String,
        val timestamp: Int,
    ) : NewsUiEvent()

    data class MarkAllAsRead(val messageIds: List<Long>) : NewsUiEvent()
    data class Translate(val text: String) : NewsUiEvent()
    data object ToggleSavedView : NewsUiEvent()
    data object DismissTranslation : NewsUiEvent()
}
