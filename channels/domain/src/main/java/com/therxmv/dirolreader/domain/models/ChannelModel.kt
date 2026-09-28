package com.therxmv.dirolreader.domain.models

data class ChannelModel(
    val id: Long,
    val unreadCount: Int,
    val lastReadMessageId: Long,
    val rating: Int = 0,
    val title: String = "",
    val isMuted: Boolean = false,
    val order: Int = 0,
    val group: String = "",
)
