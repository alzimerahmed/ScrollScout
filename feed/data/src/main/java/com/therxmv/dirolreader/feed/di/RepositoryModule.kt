package com.therxmv.dirolreader.feed.di

import com.therxmv.dirolreader.data.repository.MessageRepositoryImpl
import com.therxmv.dirolreader.data.repository.SavedMessagesRepositoryImpl
import com.therxmv.dirolreader.data.repository.TranslationRepositoryImpl
import com.therxmv.dirolreader.data.repository.UserRepositoryImpl
import com.therxmv.dirolreader.domain.repository.MessageRepository
import com.therxmv.dirolreader.domain.repository.SavedMessagesRepository
import com.therxmv.dirolreader.domain.repository.TranslationRepository
import com.therxmv.dirolreader.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsMessageRepository(repository: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindsUserRepository(repository: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindsSavedMessagesRepository(repository: SavedMessagesRepositoryImpl): SavedMessagesRepository

    @Binds
    @Singleton
    abstract fun bindsTranslationRepository(repository: TranslationRepositoryImpl): TranslationRepository
}
