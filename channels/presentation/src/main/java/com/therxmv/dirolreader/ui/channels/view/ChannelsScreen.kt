package com.therxmv.dirolreader.ui.channels.view

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.therxmv.common.R
import com.therxmv.common.commonview.CenteredBoxLoader
import com.therxmv.common.commonview.CenteredTopBar
import com.therxmv.common.commonview.DefaultTitle
import com.therxmv.dirolreader.ui.channels.viewmodel.ChannelsViewModel
import com.therxmv.dirolreader.ui.channels.viewmodel.utils.ChannelItem
import com.therxmv.dirolreader.ui.channels.viewmodel.utils.ChannelsUiState
import kotlinx.collections.immutable.PersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsScreen(
    navController: NavController,
    viewModel: ChannelsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val muteFailed by viewModel.muteFailed.collectAsStateWithLifecycle(initialValue = null)
    val context = LocalContext.current

    LaunchedEffect(muteFailed) {
        if (muteFailed != null) {
            Toast.makeText(
                context,
                R.string.channels_mute_failed,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    Scaffold(
        topBar = {
            CenteredTopBar(
                title = { DefaultTitle(title = stringResource(id = R.string.channels_title)) },
                navController = navController,
            )
        },
        contentWindowInsets = WindowInsets(bottom = 0),
    ) { padding ->
        Crossfade(targetState = uiState, label = "content") { state ->
            when (state) {
                is ChannelsUiState.Loading -> CenteredBoxLoader()
                is ChannelsUiState.Empty -> EmptyChannels(modifier = Modifier.padding(padding))
                is ChannelsUiState.Error -> ErrorState(modifier = Modifier.padding(padding))
                is ChannelsUiState.Ready -> ChannelsList(
                    screenPadding = padding,
                    channels = state.channels,
                    onToggleMute = viewModel::onToggleMute,
                    onMove = viewModel::onMove,
                    onSetGroup = viewModel::onSetGroup,
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
private fun EmptyChannels(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.channels_empty),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ChannelsList(
    screenPadding: PaddingValues,
    channels: PersistentList<ChannelItem>,
    onToggleMute: (Long, Boolean) -> Unit,
    onMove: (Long, Boolean) -> Unit,
    onSetGroup: (Long, String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(screenPadding)
            .fillMaxSize(),
    ) {
        items(channels, key = { it.id }) { channel ->
            ChannelRow(
                channel = channel,
                onToggleMute = onToggleMute,
                onMove = onMove,
                onSetGroup = onSetGroup,
            )
        }
    }
}

@Composable
private fun ChannelRow(
    channel: ChannelItem,
    onToggleMute: (Long, Boolean) -> Unit,
    onMove: (Long, Boolean) -> Unit,
    onSetGroup: (Long, String) -> Unit,
) {
    var showGroupDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (channel.unreadCount > 0) {
                    Text(
                        text = "${channel.unreadCount} ${stringResource(id = R.string.news_unread_channels)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Switch(
                checked = !channel.isMuted,
                onCheckedChange = { enabled -> onToggleMute(channel.id, !enabled) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onMove(channel.id, true) },
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_up_icon),
                    contentDescription = stringResource(id = R.string.channels_move_up),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = { onMove(channel.id, false) },
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down_icon),
                    contentDescription = stringResource(id = R.string.channels_move_down),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            TextButton(onClick = { showGroupDialog = true }) {
                Text(
                    text = channel.group.ifBlank {
                        stringResource(id = R.string.channels_set_group)
                    },
                )
            }
        }
    }

    if (showGroupDialog) {
        GroupDialog(
            initial = channel.group,
            onConfirm = { group ->
                onSetGroup(channel.id, group)
                showGroupDialog = false
            },
            onDismiss = { showGroupDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupDialog(
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.channels_group_label)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(text = stringResource(id = R.string.channels_group_hint)) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}
