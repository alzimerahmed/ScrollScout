package com.therxmv.dirolreader.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.therxmv.common.Room.CACHED_MESSAGE_TABLE

/**
 * Snapshot of a feed post cached at fetch time. Powers full-text search (via
 * [CachedMessageFtsEntity]) and the offline feed fallback. Text-only: media
 * metadata is not part of the snapshot, cached posts render without media.
 */
@Entity(tableName = CACHED_MESSAGE_TABLE)
data class CachedMessageEntity(
    @PrimaryKey val messageId: Long,
    @ColumnInfo(name = "channelId") val channelId: Long,
    @ColumnInfo(name = "channelName") val channelName: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "timestamp") val timestamp: Int,
    @ColumnInfo(name = "cachedAt") val cachedAt: Long,
)
