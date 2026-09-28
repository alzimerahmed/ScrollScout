package com.therxmv.dirolreader.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.therxmv.common.Room.CACHED_MESSAGE_FTS_TABLE

/**
 * Standalone FTS4 index over cached post text. `rowid` mirrors `messageId`
 * so the index is populated explicitly alongside the content table — no
 * content-sync triggers needed in the migration (ADR-009).
 */
@Fts4
@Entity(tableName = CACHED_MESSAGE_FTS_TABLE)
data class CachedMessageFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid") val messageId: Long,
    @ColumnInfo(name = "text") val text: String,
)
