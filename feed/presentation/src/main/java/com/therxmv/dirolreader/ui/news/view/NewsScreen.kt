package com.therxmv.dirolreader.ui.news.view

import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.therxmv.common.R
import com.therxmv.common.commonview.CenteredTopBar
import com.therxmv.dirolreader.ui.news.view.post.EmptyAvatar
import com.therxmv.dirolreader.ui.news.viewmodel.FeedViewModel
import com.therxmv.dirolreader.ui.news.viewmodel.utils.FeedUiState
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent
import com.therxmv.dirolreader.ui.news.viewmodel.utils.ToolbarState
import com.therxmv.dirolreader.ui.news.viewmodel.utils.TranslationState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    navController: NavController,
    onNavigateToProfile: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val uiState = viewModel.uiState.collectAsState().value
    val news = viewModel.news.collectAsLazyPagingItems()
    val starredChannels by viewModel.starredChannels.collectAsState()
    val savedMessages by viewModel.savedMessages.collectAsState()
    val isSavedView by viewModel.isSavedView.collectAsState()
    val translationState by viewModel.translationState.collectAsState()

    Scaffold(
        topBar = {
            if (uiState is FeedUiState.Ready) {
                NewsTopBar(
                    state = uiState.toolbarState,
                    navController = navController,
                    isSavedView = isSavedView,
                    actions = NewsTopBarActions(
                        onAvatarClick = onNavigateToProfile,
                        scrollToTop = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        },
                        onToggleSavedView = { viewModel.onEvent(NewsUiEvent.ToggleSavedView) },
                        onMarkAllAsRead = {
                            viewModel.onEvent(
                                NewsUiEvent.MarkAllAsRead(
                                    messageIds = news.itemSnapshotList
                                        .filterNotNull()
                                        .map { post -> post.id },
                                ),
                            )
                        },
                    ),
                )
            }
        },
        contentWindowInsets = WindowInsets(bottom = 0),
    ) { padding ->
        Crossfade(
            targetState = isSavedView,
            label = "content",
        ) { savedView ->
            when {
                savedView -> SavedNewsContent(
                    modifier = Modifier.padding(padding),
                    savedMessages = savedMessages,
                    starredChannels = starredChannels.toPersistentList(),
                    onEvent = viewModel::onEvent,
                    loadMedia = viewModel::loadMessageMedia,
                )

                uiState is FeedUiState.Ready -> NewsScreenContent(
                    modifier = Modifier.padding(padding),
                    listState = listState,
                    news = news,
                    starredChannels = starredChannels.toPersistentList(),
                    onEvent = viewModel::onEvent,
                    loadMedia = viewModel::loadMessageMedia,
                )
            }
        }
    }

    TranslationDialog(
        state = translationState,
        onDismiss = { viewModel.onEvent(NewsUiEvent.DismissTranslation) },
    )
}

@Composable
private fun TranslationDialog(
    state: TranslationState,
    onDismiss: () -> Unit,
) {
    if (state == TranslationState.Idle) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.news_translation_title))
        },
        text = {
            when (state) {
                is TranslationState.Loading -> CircularProgressIndicator()
                is TranslationState.Ready -> Text(text = state.text)
                is TranslationState.Error -> Text(
                    text = stringResource(id = R.string.news_translation_error),
                    color = MaterialTheme.colorScheme.error,
                )
                is TranslationState.Idle -> {}
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
    )
}

data class NewsTopBarActions(
    val onAvatarClick: () -> Unit,
    val scrollToTop: () -> Unit,
    val onToggleSavedView: () -> Unit,
    val onMarkAllAsRead: () -> Unit,
)

@Composable
private fun NewsTopBar(
    state: ToolbarState,
    navController: NavController,
    isSavedView: Boolean,
    actions: NewsTopBarActions,
) {
    val interactionSource = remember { MutableInteractionSource() }
    CenteredTopBar(
        title = {
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = actions.scrollToTop,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${state.unreadChannels} ${stringResource(id = R.string.news_unread_channels)}",
                )
            }
        },
        navController = navController,
        actions = {
            IconButton(onClick = actions.onMarkAllAsRead) {
                Icon(
                    painter = painterResource(id = R.drawable.mark_all_read_icon),
                    contentDescription = stringResource(id = R.string.news_mark_all_read),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(onClick = actions.onToggleSavedView) {
                Icon(
                    painter = painterResource(
                        id = if (isSavedView) R.drawable.bookmark_filled_icon else R.drawable.bookmark_outline_icon,
                    ),
                    contentDescription = stringResource(id = R.string.news_saved),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Avatar(
                state = state,
                onAvatarClick = actions.onAvatarClick,
            )
        },
    )
}

@Composable
private fun Avatar(
    state: ToolbarState,
    onAvatarClick: () -> Unit,
) {
    if (state.avatarPath.isNotBlank()) {
        Image(
            bitmap = BitmapFactory.decodeFile(state.avatarPath).asImageBitmap(),
            contentDescription = "Avatar",
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .clip(
                    MaterialTheme.shapes.small,
                )
                .clickable(onClick = onAvatarClick),
        )
    } else {
        EmptyAvatar(
            modifier = Modifier
                .clickable(onClick = onAvatarClick),
            name = state.userName,
        )
    }
}
