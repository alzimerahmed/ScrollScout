package com.therxmv.dirolreader.ui.news.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.therxmv.common.R
import com.therxmv.common.commonview.CenteredBoxLoader
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.ui.news.view.post.ChannelUiData
import com.therxmv.dirolreader.ui.news.view.post.NewsPost
import com.therxmv.dirolreader.ui.news.view.post.NewsPostActions
import com.therxmv.dirolreader.ui.news.view.post.NewsPostHandlers
import com.therxmv.dirolreader.ui.news.view.post.NewsPostUiData
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent.Dislike
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent.Like
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent.MarkAsRead
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent.SaveMessage
import com.therxmv.dirolreader.ui.news.viewmodel.utils.NewsUiEvent.StarChannel
import com.therxmv.common.thenIf
import kotlinx.collections.immutable.PersistentList

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun NewsScreenContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    news: LazyPagingItems<NewsPostUiData>,
    starredChannels: PersistentList<Long>,
    handlers: NewsPostHandlers,
) {
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false, // Handled by paging refresh state
        onRefresh = {
            news.refresh()
        },
    )

    Box(
        modifier = modifier
            .thenIf(isRefreshing.not()) {
                pullRefresh(pullRefreshState)
            },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
        ) {
            handlePagingState(state = news.loadState.refresh)
            handlePagingState(state = news.loadState.prepend)

            emptyNewsMessage(
                isVisible = news.itemCount == 0 && news.loadState.refresh !is LoadState.Loading,
            )

            items(
                count = news.itemCount,
                key = news.itemKey(key = { it.id }),
                contentType = news.itemContentType(),
            ) { index ->
                news[index]?.let { post ->
                    val channelId = post.channelData.id
                    val actions = NewsPostActions(
                        onStarChannel = {
                            handlers.onEvent(StarChannel(channelId = channelId, isStarred = it))
                        },
                        onLike = {
                            handlers.onEvent(Like(channelId = channelId, isLiked = it))
                        },
                        onDislike = {
                            handlers.onEvent(Dislike(channelId = channelId, isLiked = it))
                        },
                        markAsRead = {
                            handlers.onEvent(MarkAsRead(messageId = post.id, channelId = channelId))
                        },
                        onTranslate = { text ->
                            handlers.onEvent(NewsUiEvent.Translate(text = text))
                        },
                    )
                    val dismissState = rememberDismissState(
                        confirmStateChange = { value ->
                            when (value) {
                                DismissValue.DismissedToEnd -> {
                                    handlers.onEvent(NewsUiEvent.Dismiss(messageId = post.id))
                                    true
                                }
                                DismissValue.DismissedToStart -> {
                                    handlers.onEvent(
                                        SaveMessage(
                                            messageId = post.id,
                                            channelId = channelId,
                                            channelName = post.channelData.name,
                                            text = post.text,
                                            timestamp = post.timestamp,
                                        ),
                                    )
                                    true
                                }
                                else -> false
                            }
                        },
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier.animateItemPlacement(),
                        background = { SwipeBackground(targetValue = dismissState.targetValue) },
                        dismissContent = {
                            NewsPost(
                                data = post,
                                handlers = handlers,
                                isStarred = starredChannels.contains(channelId),
                                actions = actions,
                            )
                        },
                    )
                }
            }

            handlePagingState(state = news.loadState.append)
        }
        PullRefreshIndicator(
            refreshing = false, // Handled by paging refresh state
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }

    LaunchedEffect(news.loadState.refresh) {
        isRefreshing = news.loadState.refresh is LoadState.Loading
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeBackground(targetValue: DismissValue) {
    val (iconRes, alignment, containerColor) = when (targetValue) {
        DismissValue.DismissedToStart -> Triple(
            R.drawable.bookmark_filled_icon,
            Alignment.CenterStart,
            MaterialTheme.colorScheme.primary,
        )
        DismissValue.DismissedToEnd -> Triple(
            R.drawable.mark_all_read_icon,
            Alignment.CenterEnd,
            MaterialTheme.colorScheme.primaryContainer,
        )
        else -> return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(containerColor),
        contentAlignment = alignment,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(
                id = if (targetValue == DismissValue.DismissedToStart) {
                    R.string.news_bookmark_add
                } else {
                    R.string.news_mark_read
                },
            ),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedNewsContent(
    modifier: Modifier = Modifier,
    savedMessages: List<MessageModel>,
    starredChannels: PersistentList<Long>,
    handlers: NewsPostHandlers,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (savedMessages.isEmpty()) {
            item {
                AnimatedVisibility(visible = true) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 150.dp),
                        text = stringResource(id = R.string.news_saved_empty),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        items(
            count = savedMessages.size,
            key = { savedMessages[it].id },
        ) { index ->
            val post = savedMessages[index]
            val channelId = post.channelData.id

            NewsPost(
                data = post.toPresentation(),
                handlers = handlers,
                isStarred = starredChannels.contains(channelId),
                actions = NewsPostActions(
                    onStarChannel = {
                        handlers.onEvent(StarChannel(channelId = channelId, isStarred = it))
                    },
                    onLike = {
                        handlers.onEvent(Like(channelId = channelId, isLiked = it))
                    },
                    onDislike = {
                        handlers.onEvent(Dislike(channelId = channelId, isLiked = it))
                    },
                    markAsRead = {},
                    onTranslate = { text ->
                        handlers.onEvent(NewsUiEvent.Translate(text = text))
                    },
                ),
            )
        }
    }
}

private fun MessageModel.toPresentation() = NewsPostUiData(
    id = id,
    text = text,
    timestamp = timestamp,
    mediaList = null,
    channelData = ChannelUiData(
        id = channelData.id,
        name = channelData.name,
        avatarPath = null,
        postTime = "",
    ),
)

private fun LazyListScope.emptyNewsMessage(isVisible: Boolean) {
    item {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(750)),
            exit = fadeOut(animationSpec = tween(200)),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 150.dp),
                text = stringResource(id = R.string.news_all_read),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.handlePagingState(state: LoadState) {
    item {
        AnimatedVisibility(
            visible = state is LoadState.Loading,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
        ) {
            CenteredBoxLoader(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .animateItemPlacement(),
            )
        }
    }
}
