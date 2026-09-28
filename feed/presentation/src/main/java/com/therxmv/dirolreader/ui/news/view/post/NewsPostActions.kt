package com.therxmv.dirolreader.ui.news.view.post

data class NewsPostActions(
    val onStarChannel: (Boolean) -> Unit,
    val onLike: (Boolean?) -> Unit,
    val onDislike: (Boolean?) -> Unit,
    val markAsRead: () -> Unit,
    val onTranslate: (String) -> Unit,
)
