package com.therxmv.dirolreader.ui.relevance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.usecase.channel.AdjustChannelWeightUseCase
import com.therxmv.dirolreader.domain.usecase.channel.GetChannelsUseCase
import com.therxmv.dirolreader.domain.usecase.channel.ResetChannelWeightsUseCase
import com.therxmv.dirolreader.ui.relevance.viewmodel.utils.RelevanceUiState
import com.therxmv.dirolreader.ui.relevance.viewmodel.utils.WeightItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelevanceViewModel @Inject constructor(
    private val getChannels: GetChannelsUseCase,
    private val adjustWeight: AdjustChannelWeightUseCase,
    private val resetWeights: ResetChannelWeightsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RelevanceUiState>(RelevanceUiState.Loading)
    val uiState: StateFlow<RelevanceUiState> = _uiState.asStateFlow()

    private var hasRefreshed = false

    init {
        getChannels.observe()
            .map { list -> list.sortedByDescending { it.rating } }
            .onEach(::updateState)
            .launchIn(viewModelScope)
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            try {
                getChannels.refresh()
            } finally {
                hasRefreshed = true
            }
        }
    }

    private fun updateState(list: List<ChannelModel>) {
        _uiState.value = when {
            list.isNotEmpty() -> {
                val maxRating = maxOf(list.maxOf { it.rating }, 1)
                RelevanceUiState.Ready(
                    items = list
                        .map { it.toItem(maxRating) }
                        .toPersistentList(),
                    canReset = list.any { it.rating != 0 },
                )
            }
            hasRefreshed -> RelevanceUiState.Empty
            else -> RelevanceUiState.Loading
        }
    }

    fun onAdjust(id: Long, delta: Int) {
        viewModelScope.launch {
            adjustWeight(id, delta)
        }
    }

    fun onReset() {
        viewModelScope.launch {
            resetWeights()
        }
    }
}

private fun ChannelModel.toItem(maxRating: Int) = WeightItem(
    id = id,
    title = title.ifBlank { "#$id" },
    rating = rating,
    maxRating = maxRating,
)
