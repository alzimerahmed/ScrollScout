package com.therxmv.dirolreader.ui.channels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.usecase.channel.GetChannelsUseCase
import com.therxmv.dirolreader.domain.usecase.channel.ReorderChannelUseCase
import com.therxmv.dirolreader.domain.usecase.channel.SetChannelGroupUseCase
import com.therxmv.dirolreader.domain.usecase.channel.SetChannelMutedUseCase
import com.therxmv.dirolreader.ui.channels.viewmodel.utils.ChannelItem
import com.therxmv.dirolreader.ui.channels.viewmodel.utils.ChannelsUiState
import com.therxmv.dirolreader.ui.channels.viewmodel.utils.toItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val getChannels: GetChannelsUseCase,
    private val setChannelMuted: SetChannelMutedUseCase,
    private val reorderChannel: ReorderChannelUseCase,
    private val setChannelGroup: SetChannelGroupUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChannelsUiState>(ChannelsUiState.Loading)
    val uiState: StateFlow<ChannelsUiState> = _uiState.asStateFlow()

    private val _muteFailed = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val muteFailed: SharedFlow<Long> = _muteFailed.asSharedFlow()

    private var channels: List<ChannelModel> = emptyList()
    private var hasRefreshed = false

    init {
        observeChannels()
        refreshChannels()
    }

    private fun observeChannels() {
        getChannels.observe()
            .map { list -> list.sortedWith(compareBy({ it.order }, { it.title })) }
            .onEach(::updateState)
            .launchIn(viewModelScope)
    }

    private fun refreshChannels() {
        viewModelScope.launch {
            try {
                getChannels.refresh()
            } finally {
                hasRefreshed = true
                updateState(channels)
            }
        }
    }

    private fun updateState(list: List<ChannelModel>) {
        channels = list
        _uiState.value = when {
            list.isNotEmpty() -> ChannelsUiState.Ready(list.map { it.toItem() }.toPersistentList())
            hasRefreshed -> ChannelsUiState.Empty
            else -> ChannelsUiState.Loading
        }
    }

    fun onToggleMute(id: Long, isMuted: Boolean) {
        viewModelScope.launch {
            val success = setChannelMuted(id, isMuted)
            if (!success) {
                _muteFailed.emit(id)
            }
        }
    }

    fun onMove(id: Long, up: Boolean) {
        val sorted = channels.sortedWith(compareBy({ it.order }, { it.title }))
        val index = sorted.indexOfFirst { it.id == id }
        val target = if (up) index - 1 else index + 1
        if (index < 0 || target !in sorted.indices) return

        val reordered = sorted.toMutableList().apply {
            val moved = removeAt(index)
            add(target, moved)
        }

        viewModelScope.launch {
            reordered.forEachIndexed { position, channel ->
                if (channel.order != position) {
                    reorderChannel(channel.id, position)
                }
            }
        }
    }

    fun onSetGroup(id: Long, group: String) {
        viewModelScope.launch {
            setChannelGroup(id, group)
        }
    }
}
