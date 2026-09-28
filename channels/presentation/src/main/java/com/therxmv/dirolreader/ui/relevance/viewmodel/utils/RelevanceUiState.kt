package com.therxmv.dirolreader.ui.relevance.viewmodel.utils

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList

sealed interface RelevanceUiState {
    data object Loading : RelevanceUiState
    data object Empty : RelevanceUiState
    data object Error : RelevanceUiState
    data class Ready(
        val items: PersistentList<WeightItem>,
        val canReset: Boolean,
    ) : RelevanceUiState
}

@Immutable
data class WeightItem(
    val id: Long,
    val title: String,
    val rating: Int,
    val maxRating: Int,
)
