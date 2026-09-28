package com.therxmv.dirolreader.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import com.therxmv.common.Room.CACHED_MESSAGE_FTS_TABLE

/**
 * Standalone FTS4 index over cached post text. Carries (channelId, messageId)
 * so results join back to the content table on the composite key — TdLib
 * message ids are only unique per chat. The implicit `rowid`/docid is
 * auto-assigned; rows are deleted before re-insert to keep the index in sync
 * (ADR-009).
 */
@Fts4
@Entity(tableName = CACHED_MESSAGE_FTS_TABLE)
data class CachedMessageFtsEntity(
    @ColumnInfo(name = "channelId") val channelId: Long,
    @ColumnInfo(name = "messageId") val messageId: Long,
    @ColumnInfo(name = "text") val text: String,
)
