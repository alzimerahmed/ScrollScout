package com.therxmv.dirolreader.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.therxmv.common.Room.CHANNEL_TABLE
import com.therxmv.dirolreader.domain.models.ChannelModel

@Entity(tableName = CHANNEL_TABLE)
data class ChannelEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "unreadCount") val unreadCount: Int,
    @ColumnInfo(name = "lastReadMessageId") val lastReadMessageId: Long,
    @ColumnInfo(name = "rating", defaultValue = "0") val rating: Int,
    @ColumnInfo(name = "title", defaultValue = "") val title: String = "",
    @ColumnInfo(name = "isMuted", defaultValue = "0") val isMuted: Boolean = false,
    @ColumnInfo(name = "sortOrder", defaultValue = "0") val sortOrder: Int = 0,
    @ColumnInfo(name = "groupName", defaultValue = "") val groupName: String = "",
)

fun ChannelEntity.toDomain() = ChannelModel(
    this.id,
    this.unreadCount,
    this.lastReadMessageId,
    this.rating,
    this.title,
    this.isMuted,
    this.sortOrder,
    this.groupName,
)

fun ChannelModel.toEntity() = ChannelEntity(
    this.id,
    this.unreadCount,
    this.lastReadMessageId,
    this.rating,
    this.title,
    this.isMuted,
    this.order,
    this.group,
)
