package com.therxmv.dirolreader.data.source.remote.message

import android.util.Log
import com.therxmv.dirolreader.data.entity.ChannelEntity
import com.therxmv.dirolreader.data.entity.toDomain
import com.therxmv.dirolreader.data.source.local.db.DirolDao
import com.therxmv.dirolreader.data.source.remote.channel.ChannelRemoteSource
import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.models.PollModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import org.drinkless.tdlib.TdApi.Chat
import org.drinkless.tdlib.TdApi.Message
import org.drinkless.tdlib.TdApi.Messages
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageRemoteDataSource @Inject constructor(
    private val client: Client,
    private val channelRemoteDataSource: ChannelRemoteSource,
    private val dirolDao: DirolDao,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) : MessageSource {

    /**
     * Used to remember last loaded message of current channel.
     * Because TdApi doesn't provide good offset to get messages properly for pagination
     */
    private var lastMessage: MessageModel? = null

    private var allUnreadChannelsFlow: MutableStateFlow<List<ChannelEntity>> = MutableStateFlow(emptyList())
    private var pagedChannels: Map<Int, List<ChannelEntity>> = emptyMap()

    override fun getUnreadChannelsFlow() = allUnreadChannelsFlow

    private suspend fun refreshData() {
        delay(500) // TODO 3 doesn't load actual channel data
        val list = channelRemoteDataSource.getChannelsForPaging()

        lastMessage = null
        allUnreadChannelsFlow.update { list }
        pagedChannels = list.sortMessagesByPage()
    }

    override suspend fun getUnreadMessagesByPage(page: Int): List<MessageModel> =
        withContext(ioDispatcher) {
            // fetches new data about channels on start and refresh
            if (page == 0 || allUnreadChannelsFlow.value.isEmpty() || pagedChannels.isEmpty()) {
                refreshData()
            }

            val channels = pagedChannels[page] ?: return@withContext emptyList()
            Log.d("rozmi", "pages: ${pagedChannels.size} page: $page - $channels")

            val messages = channels.map {
                async {
                    val channel = it.toDomain()
                    val channelData = getChannelData(channel)
                    val history = getChannelHistory(channel)

                    history.map { message ->
                        handleMessageType(channelData, message)
                    }
                }
            }.awaitAll()

            return@withContext messages
                .flatten()
                .saveLastAndDrop(page)
                .groupMediaMessagesInOne()
        }

    private suspend fun getChannelHistory(channel: ChannelModel): List<Message> =
        suspendCoroutine {
            val unreadCount = channel.unreadCount

            val request = if (unreadCount == 1) { // Better to use 0 for one unread message
                TdApi.GetChatHistory(
                    channel.id, // chatId
                    0, // fromMessageId
                    0, // offset
                    1, // limit
                    false, // onlyLocal
                )
            } else {
                val limit = channel.unreadCount + 1 // Plus one, because we have last READ messageId
                val localId = lastMessage?.id
                    ?.takeIf { lastMessage?.channelData?.id == channel.id }

                val messageId = localId ?: channel.lastReadMessageId
                val offset = limit * -1

                TdApi.GetChatHistory(
                    channel.id, // chatId
                    messageId, // fromMessageId
                    offset, // offset
                    limit, // limit
                    false, // onlyLocal
                )
            }

            client.send(request) { messages ->
                messages as Messages

                val list = messages.messages
                    .reversed()
                    .filter { it.id != channel.lastReadMessageId } // Need to drop last READ message

                it.resume(list)
            }
        }

    /**
     * Votes in a poll. After TdLib accepts the answer the message is re-fetched
     * so the UI can render the updated voter counts — the client has no update
     * handler wired, so nothing else pushes the new state to the feed.
     */
    override suspend fun setPollAnswer(
        chatId: Long,
        messageId: Long,
        optionIds: IntArray,
    ): PollModel? = withContext(ioDispatcher) {
        val answerResult = sendRequest(TdApi.SetPollAnswer(chatId, messageId, optionIds))
        if (answerResult is TdApi.Error) {
            return@withContext null
        }

        val updatedMessage = sendRequest(TdApi.GetMessage(chatId, messageId)) as? Message
        (updatedMessage?.content as? TdApi.MessagePoll)?.poll?.toPollModel()
    }

    private suspend fun sendRequest(request: TdApi.Function<*>) = suspendCoroutine { continuation ->
        client.send(request) { result ->
            continuation.resume(result)
        }
    }

    override suspend fun markAllAsRead() = withContext(ioDispatcher) {
        val unreadChannels = allUnreadChannelsFlow.value.filter { it.unreadCount > 0 }

        unreadChannels.forEach { channelEntity ->
            val channel = channelEntity.toDomain()
            val history = getChannelHistory(channel)
            if (history.isEmpty()) return@forEach

            val viewResult = suspendCoroutine { continuation ->
                client.send(
                    TdApi.ViewMessages(
                        channel.id, // chatId
                        history.map { it.id }.toLongArray(), // messageIds
                        null, // source
                        true, // forceRead
                    ),
                ) { result ->
                    continuation.resume(result)
                }
            }
            if (viewResult is TdApi.Error) return@forEach

            val lastReadId = history.last().id
            dirolDao.markChannelAsRead(channel.id, lastReadId)
            allUnreadChannelsFlow.update { current ->
                current.map {
                    if (it.id == channel.id) {
                        it.copy(unreadCount = 0, lastReadMessageId = lastReadId)
                    } else {
                        it
                    }
                }
            }
        }
    }

    private suspend fun getChannelData(channel: ChannelModel): ChannelData =
        suspendCoroutine { continuation ->
            client.send(TdApi.GetChat(channel.id)) { chat ->
                chat as Chat
                val smallPhotoId = chat.photo?.small?.id

                if (smallPhotoId != null) {
                    client.send(
                        TdApi.DownloadFile(
                            smallPhotoId, // fileId
                            32, // priority
                            0, // offset
                            0, // limit
                            true, // synchronous
                        ),
                    ) { file ->
                        file as TdApi.File

                        continuation.resume(
                            ChannelData(
                                id = channel.id,
                                rating = channel.rating,
                                name = chat.title,
                                avatarPath = file.local.path,
                            ),
                        )
                    }
                } else {
                    continuation.resume(
                        ChannelData(
                            id = channel.id,
                            rating = channel.rating,
                            name = chat.title,
                        ),
                    )
                }
            }
        }

    private fun List<MessageModel>.saveLastAndDrop(page: Int) = this.run {
        val last = lastOrNull()
        val nextPageChannelId = pagedChannels[page + 1]?.firstOrNull()?.id

        if (last?.channelData?.id == nextPageChannelId) {
            lastMessage = last
            dropLast(1)
        } else {
            lastMessage = null
            this
        }
    }
}