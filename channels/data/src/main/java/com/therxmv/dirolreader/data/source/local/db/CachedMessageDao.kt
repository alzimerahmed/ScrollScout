package com.therxmv.dirolreader.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.therxmv.common.Room.CACHED_MESSAGE_FTS_TABLE
import com.therxmv.common.Room.CACHED_MESSAGE_TABLE
import com.therxmv.dirolreader.data.entity.CachedMessageEntity
import com.therxmv.dirolreader.data.entity.CachedMessageFtsEntity

@Dao
interface CachedMessageDao {

    @Transaction
    fun insertCachedMessage(entity: CachedMessageEntity) {
        insertCachedMessageContent(entity)
        deleteCachedMessageIndex(entity.channelId, entity.messageId)
        insertCachedMessageIndex(
            CachedMessageFtsEntity(
                channelId = entity.channelId,
                messageId = entity.messageId,
                text = entity.text,
            ),
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCachedMessageContent(entity: CachedMessageEntity)

    @Query(
        "DELETE FROM $CACHED_MESSAGE_FTS_TABLE " +
            "WHERE channelId = :channelId AND messageId = :messageId",
    )
    fun deleteCachedMessageIndex(channelId: Long, messageId: Long)

    @Insert
    fun insertCachedMessageIndex(ftsEntity: CachedMessageFtsEntity)

    @Query(
        "SELECT c.* FROM $CACHED_MESSAGE_TABLE c " +
            "JOIN $CACHED_MESSAGE_FTS_TABLE ON " +
            "$CACHED_MESSAGE_FTS_TABLE.channelId = c.channelId AND " +
            "$CACHED_MESSAGE_FTS_TABLE.messageId = c.messageId " +
            "WHERE $CACHED_MESSAGE_FTS_TABLE MATCH :query " +
            "ORDER BY c.timestamp DESC",
    )
    fun searchCachedMessages(query: String): List<CachedMessageEntity>

    @Query("SELECT * FROM $CACHED_MESSAGE_TABLE ORDER BY timestamp DESC LIMIT :limit")
    fun getCachedMessages(limit: Int): List<CachedMessageEntity>
}
