package com.therxmv.dirolreader.ui.search.view

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.therxmv.common.R
import com.therxmv.common.commonview.CenteredBoxLoader
import com.therxmv.common.commonview.CenteredTopBar
import com.therxmv.common.commonview.DefaultTitle
import com.therxmv.dirolreader.ui.search.viewmodel.SearchViewModel
import com.therxmv.dirolreader.ui.search.viewmodel.utils.SearchResultItem
import com.therxmv.dirolreader.ui.search.viewmodel.utils.SearchUiState
import kotlinx.collections.immutable.PersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenteredTopBar(
                title = { DefaultTitle(title = stringResource(id = R.string.search_title)) },
                navController = navController,
            )
        },
        contentWindowInsets = WindowInsets(bottom = 0),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            SearchField(
                query = query,
                onQueryChange = viewModel::onQueryChange,
            )

            Crossfade(targetState = uiState, label = "content") { state ->
                when (state) {
                    is SearchUiState.Initial -> Hint(modifier = Modifier.fillMaxSize(), text = R.string.search_hint)
                    is SearchUiState.Loading -> CenteredBoxLoader()
                    is SearchUiState.NoResults -> Hint(modifier = Modifier.fillMaxSize(), text = R.string.search_no_results)
                    is SearchUiState.Error -> Hint(modifier = Modifier.fillMaxSize(), text = R.string.search_error)
                    is SearchUiState.Ready -> SearchResults(results = state.results)
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics { contentDescription = "Search query input" },
        label = { Text(text = stringResource(id = R.string.search_hint)) },
        singleLine = true,
    )
}

@Composable
private fun Hint(modifier: Modifier = Modifier, text: Int) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun SearchResults(results: PersistentList<SearchResultItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(results, key = { it.id }) { result ->
            SearchResultRow(result = result)
        }
    }
}

@Composable
private fun SearchResultRow(result: SearchResultItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = result.channelName,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = result.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = result.text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 4,
        )
    }
}
