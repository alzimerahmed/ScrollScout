package com.therxmv.dirolreader.channels.di

import android.content.Context
import androidx.room.Room
import com.therxmv.dirolreader.data.source.local.db.DirolDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalDataSourceModule {

    @Provides
    @Singleton
    fun provideDirolDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            DirolDatabase::class.java,
            "Dirol.db"
        )
            .addMigrations(
                DirolDatabase.MIGRATION_1_2,
                DirolDatabase.MIGRATION_2_3,
                DirolDatabase.MIGRATION_3_4,
            )
            .build()

    @Provides
    @Singleton
    fun provideDirolDao(database: DirolDatabase) = database.dirolDao()

    @Provides
    @Singleton
    fun provideCachedMessageDao(database: DirolDatabase) = database.cachedMessageDao()
}