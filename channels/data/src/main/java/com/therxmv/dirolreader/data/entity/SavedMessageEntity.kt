package com.therxmv.dirolreader.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.therxmv.common.Room.SAVED_MESSAGE_TABLE

/**
 * Snapshot of a bookmarked post. Stores channel name and text at save time,
 * because saved posts must stay readable after the source message is marked
 * as read and drops out of the unread feed.
 */
@Entity(tableName = SAVED_MESSAGE_TABLE)
data class SavedMessageEntity(
    @PrimaryKey val messageId: Long,
    @ColumnInfo(name = "channelId") val channelId: Long,
    @ColumnInfo(name = "channelName") val channelName: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "timestamp") val timestamp: Int,
    @ColumnInfo(name = "savedAt") val savedAt: Long,
)
