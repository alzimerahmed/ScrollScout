package com.therxmv.dirolreader.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.therxmv.common.Room.CHANNEL_TABLE
import com.therxmv.common.Room.SAVED_MESSAGE_TABLE
import com.therxmv.dirolreader.data.entity.ChannelEntity
import com.therxmv.dirolreader.data.entity.SavedMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DirolDao {

    @Query("SELECT * FROM $CHANNEL_TABLE")
    fun getAllChannels(): List<ChannelEntity>

    @Query("UPDATE $CHANNEL_TABLE SET rating = rating + :num WHERE id = :id")
    fun updateChannelRating(id: Long, num: Int)

    @Query("UPDATE $CHANNEL_TABLE SET unreadCount = :unreadCount, lastReadMessageId = :lastId WHERE id = :id")
    fun updateChannel(id: Long, unreadCount: Int, lastId: Long): Int

    @Query("UPDATE $CHANNEL_TABLE SET unreadCount = 0, lastReadMessageId = :lastId WHERE id = :id")
    fun markChannelAsRead(id: Long, lastId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChannel(channelEntity: ChannelEntity): Long

    @Transaction
    fun insertOrUpdateChannel(channelEntity: ChannelEntity): Int {
        val id = addChannel(channelEntity)
        return if (id == -1L) {
            updateChannel(
                channelEntity.id,
                channelEntity.unreadCount,
                channelEntity.lastReadMessageId
            )
        } else id.toInt()
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSavedMessage(savedMessageEntity: SavedMessageEntity)

    @Query("DELETE FROM $SAVED_MESSAGE_TABLE WHERE messageId = :messageId")
    fun deleteSavedMessage(messageId: Long)

    @Query("SELECT * FROM $SAVED_MESSAGE_TABLE ORDER BY savedAt DESC")
    fun getSavedMessagesFlow(): Flow<List<SavedMessageEntity>>
}