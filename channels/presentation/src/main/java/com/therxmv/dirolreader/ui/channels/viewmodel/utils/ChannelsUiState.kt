package com.therxmv.dirolreader.ui.channels.viewmodel.utils

import androidx.compose.runtime.Immutable
import com.therxmv.dirolreader.domain.models.ChannelModel
import kotlinx.collections.immutable.PersistentList

sealed interface ChannelsUiState {
    data object Loading : ChannelsUiState
    data object Empty : ChannelsUiState
    data object Error : ChannelsUiState
    data class Ready(val channels: PersistentList<ChannelItem>) : ChannelsUiState
}

@Immutable
data class ChannelItem(
    val id: Long,
    val title: String,
    val unreadCount: Int,
    val isMuted: Boolean,
    val group: String,
)

fun ChannelModel.toItem() = ChannelItem(
    id = id,
    title = title.ifBlank { "#$id" },
    unreadCount = unreadCount,
    isMuted = isMuted,
    group = group,
)
