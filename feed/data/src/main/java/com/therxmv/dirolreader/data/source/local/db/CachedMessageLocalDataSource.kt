package com.therxmv.dirolreader.data.source.local.db

import com.therxmv.dirolreader.data.entity.CachedMessageEntity
import com.therxmv.dirolreader.domain.models.ChannelData
import com.therxmv.dirolreader.domain.models.MessageModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

/**
 * Local cache of feed posts. Write-through: every page fetched from TdLib is
 * snapshotted here, so search (FTS4) and the offline fallback work over posts
 * that have already been loaded at least once.
 */
class CachedMessageLocalDataSource @Inject constructor(
    private val dirolDao: DirolDao,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun saveMessages(messages: List<MessageModel>) = withContext(ioDispatcher) {
        messages.forEach { dirolDao.insertCachedMessage(it.toCachedEntity()) }
    }

    suspend fun searchMessages(query: String): List<MessageModel> = withContext(ioDispatcher) {
        val ftsQuery = query.toFtsQuery()
        if (ftsQuery == null) {
            emptyList()
        } else {
            dirolDao.searchCachedMessages(ftsQuery).map { it.toDomain() }
        }
    }

    suspend fun getCachedMessages(limit: Int): List<MessageModel> = withContext(ioDispatcher) {
        dirolDao.getCachedMessages(limit).map { it.toDomain() }
    }

    /**
     * Quote each token so FTS syntax characters in user input (quotes,
     * hyphens, colons…) are treated as literal text. Returns null when the
     * query has no searchable tokens.
     */
    private fun String.toFtsQuery(): String? =
        split(' ')
            .map { it.replace("\"", "") }
            .filter { it.isNotBlank() }
            .joinToString(" ") { "\"$it\"" }
            .takeIf { it.isNotBlank() }
}

private fun MessageModel.toCachedEntity() = CachedMessageEntity(
    messageId = id,
    channelId = channelData.id,
    channelName = channelData.name,
    text = text,
    timestamp = timestamp,
    cachedAt = System.currentTimeMillis(),
)

private fun CachedMessageEntity.toDomain() = MessageModel(
    id = messageId,
    channelData = ChannelData(
        id = channelId,
        rating = 0,
        name = channelName,
    ),
    timestamp = timestamp,
    text = text,
    mediaList = null,
)
