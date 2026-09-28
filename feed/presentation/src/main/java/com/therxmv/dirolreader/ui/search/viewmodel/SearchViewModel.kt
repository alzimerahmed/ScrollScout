package com.therxmv.dirolreader.ui.search.viewmodel

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.usecase.NewsViewModelUseCases
import com.therxmv.dirolreader.ui.search.viewmodel.utils.SearchResultItem
import com.therxmv.dirolreader.ui.search.viewmodel.utils.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val useCases: NewsViewModelUseCases,
) : ViewModel() {

    private companion object {
        const val TAG = "SearchViewModel"
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .distinctUntilChanged()
                .flatMapLatest { query -> search(query) }
                .collect { state -> _uiState.update { state } }
        }
    }

    fun onQueryChange(query: String) {
        _query.update { query }
    }

    private fun search(query: String) = flow {
        if (query.isBlank()) {
            emit(SearchUiState.Initial)
            return@flow
        }

        emit(SearchUiState.Loading)

        val results = try {
            useCases.searchMessages(query)
        } catch (e: IOException) {
            Log.w(TAG, "Search failed for query: $query", e)
            emit(SearchUiState.Error)
            return@flow
        } catch (e: SQLiteException) {
            Log.w(TAG, "Search failed for query: $query", e)
            emit(SearchUiState.Error)
            return@flow
        }

        emit(
            if (results.isEmpty()) {
                SearchUiState.NoResults
            } else {
                SearchUiState.Ready(results.toItems())
            },
        )
    }

    private fun List<MessageModel>.toItems() =
        map { message ->
            SearchResultItem(
                id = message.id,
                channelName = message.channelData.name,
                time = useCases.getReadablePostTime(message.timestamp),
                text = message.text,
            )
        }.toPersistentList()
}
