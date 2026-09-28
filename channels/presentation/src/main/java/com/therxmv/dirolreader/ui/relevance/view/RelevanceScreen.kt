package com.therxmv.dirolreader.ui.relevance.view

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.therxmv.dirolreader.ui.relevance.viewmodel.RelevanceViewModel
import com.therxmv.dirolreader.ui.relevance.viewmodel.utils.RelevanceUiState
import com.therxmv.dirolreader.ui.relevance.viewmodel.utils.WeightItem
import kotlinx.collections.immutable.PersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelevanceScreen(
    navController: NavController,
    viewModel: RelevanceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenteredTopBar(
                title = { DefaultTitle(title = stringResource(id = R.string.relevance_title)) },
                navController = navController,
                actions = {
                    if (uiState is RelevanceUiState.Ready && (uiState as RelevanceUiState.Ready).canReset) {
                        TextButton(onClick = viewModel::onReset) {
                            Text(text = stringResource(id = R.string.relevance_reset))
                        }
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(bottom = 0),
    ) { padding ->
        Crossfade(targetState = uiState, label = "content") { state ->
            when (state) {
                is RelevanceUiState.Loading -> CenteredBoxLoader()
                is RelevanceUiState.Empty -> EmptyRelevance(modifier = Modifier.padding(padding))
                is RelevanceUiState.Error -> ErrorState(modifier = Modifier.padding(padding))
                is RelevanceUiState.Ready -> WeightList(
                    screenPadding = padding,
                    items = state.items,
                    onAdjust = viewModel::onAdjust,
                )
            }
        }
    }
}

@Composable
private fun ErrorState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.channels_load_error),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun EmptyRelevance(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.relevance_empty),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun WeightList(
    screenPadding: PaddingValues,
    items: PersistentList<WeightItem>,
    onAdjust: (Long, Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(screenPadding)
            .fillMaxSize(),
    ) {
        items(items, key = { it.id }) { item ->
            WeightRow(item = item, onAdjust = onAdjust)
        }
    }
}

@Composable
private fun WeightRow(
    item: WeightItem,
    onAdjust: (Long, Int) -> Unit,
) {
    val weightLabel = stringResource(id = R.string.relevance_weight, item.rating)
    val decreaseLabel = stringResource(id = R.string.relevance_decrease)
    val increaseLabel = stringResource(id = R.string.relevance_increase)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )
            TextButton(
                onClick = { onAdjust(item.id, -WEIGHT_STEP) },
                modifier = Modifier.semantics {
                    contentDescription = decreaseLabel
                },
            ) {
                Text(text = "−")
            }
            Text(
                text = weightLabel,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            TextButton(
                onClick = { onAdjust(item.id, WEIGHT_STEP) },
                modifier = Modifier.semantics {
                    contentDescription = increaseLabel
                },
            ) {
                Text(text = "+")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                ),
        ) {
            val fraction = (item.rating.toFloat() / item.maxRating.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small,
                    ),
            )
        }
    }
}

private const val WEIGHT_STEP = 1
