package com.therxmv.dirolreader.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.therxmv.common.Room.CHANNEL_TABLE
import com.therxmv.common.Room.SAVED_MESSAGE_TABLE
import com.therxmv.dirolreader.data.entity.ChannelEntity
import com.therxmv.dirolreader.data.entity.SavedMessageEntity

@Database(
    entities = [ChannelEntity::class, SavedMessageEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class DirolDatabase : RoomDatabase() {
    abstract fun dirolDao(): DirolDao

    companion object {
        private const val SCHEMA_VERSION_2 = 2
        private const val SCHEMA_VERSION_3 = 3

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `$SAVED_MESSAGE_TABLE` (" +
                        "`messageId` INTEGER NOT NULL, " +
                        "`channelId` INTEGER NOT NULL, " +
                        "`channelName` TEXT NOT NULL, " +
                        "`text` TEXT NOT NULL, " +
                        "`timestamp` INTEGER NOT NULL, " +
                        "`savedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`messageId`))",
                )
            }
        }
        val MIGRATION_2_3 = object : Migration(SCHEMA_VERSION_2, SCHEMA_VERSION_3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `$CHANNEL_TABLE` ADD COLUMN `title` TEXT NOT NULL DEFAULT ''",
                )
                database.execSQL(
                    "ALTER TABLE `$CHANNEL_TABLE` ADD COLUMN `isMuted` INTEGER NOT NULL DEFAULT 0",
                )
                database.execSQL(
                    "ALTER TABLE `$CHANNEL_TABLE` ADD COLUMN `sortOrder` INTEGER NOT NULL DEFAULT 0",
                )
                database.execSQL(
                    "ALTER TABLE `$CHANNEL_TABLE` ADD COLUMN `groupName` TEXT NOT NULL DEFAULT ''",
                )
            }
        }
    }
}