package com.therxmv.dirolreader.ui.search.viewmodel.utils

import kotlinx.collections.immutable.PersistentList

sealed interface SearchUiState {
    data object Initial : SearchUiState
    data object Loading : SearchUiState
    data object NoResults : SearchUiState
    data object Error : SearchUiState
    data class Ready(val results: PersistentList<SearchResultItem>) : SearchUiState
}

data class SearchResultItem(
    val id: Long,
    val channelId: Long,
    val channelName: String,
    val time: String,
    val text: String,
)
