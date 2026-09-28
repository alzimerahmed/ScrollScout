package com.therxmv.dirolreader.data.repository

import android.util.Log
import com.therxmv.common.Paging
import com.therxmv.dirolreader.data.entity.toDomain
import com.therxmv.dirolreader.data.source.local.db.CachedMessageLocalDataSource
import com.therxmv.dirolreader.data.source.remote.media.MediaSource
import com.therxmv.dirolreader.data.source.remote.message.MessageSource
import com.therxmv.dirolreader.domain.models.ChannelModel
import com.therxmv.dirolreader.domain.models.MessageModel
import com.therxmv.dirolreader.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageSource,
    private val mediaRemoteDataSource: MediaSource,
    private val cachedMessageLocalDataSource: CachedMessageLocalDataSource,
) : MessageRepository {

    private companion object {
        const val TAG = "MessageRepository"
        const val FALLBACK_PAGE_SIZE = 10
    }

    override suspend fun getUnreadMessagesByPage(page: Int): List<MessageModel> =
        try {
            messageRemoteDataSource.getUnreadMessagesByPage(page)
                .also { cachedMessageLocalDataSource.saveMessages(it) }
        } catch (e: IOException) {
            fallbackMessages(page, e)
        } catch (e: ClassCastException) {
            // TdLib error objects surface as failed casts to TdApi response types
            fallbackMessages(page, e)
        }

    /**
     * Remote fetch failed (offline / TdLib error): fall back to the cached
     * snapshot instead of surfacing a paging error. Only the first page
     * serves the fallback; further pages end the list.
     */
    private suspend fun fallbackMessages(page: Int, e: Exception): List<MessageModel> {
        Log.w(TAG, "Feed page $page fetch failed, using cached posts", e)
        return if (page == Paging.STARTING_PAGE_INDEX) {
            cachedMessageLocalDataSource.getCachedMessages(limit = FALLBACK_PAGE_SIZE)
        } else {
            emptyList()
        }
    }

    override suspend fun downloadMediaAndGetPath(mediaId: Int) =
        mediaRemoteDataSource.downloadMediaAndGetPath(mediaId)

    override fun getUnreadChannelsFlow(): Flow<List<ChannelModel>> =
        messageRemoteDataSource.getUnreadChannelsFlow().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun markAllAsRead() = messageRemoteDataSource.markAllAsRead()

    override suspend fun searchMessages(query: String): List<MessageModel> =
        cachedMessageLocalDataSource.searchMessages(query)

    override suspend fun getCachedMessages(limit: Int): List<MessageModel> =
        cachedMessageLocalDataSource.getCachedMessages(limit)
}
