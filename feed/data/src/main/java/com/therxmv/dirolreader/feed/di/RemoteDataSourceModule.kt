package com.therxmv.dirolreader.feed.di

import com.therxmv.dirolreader.data.source.remote.media.MediaRemoteDataSource
import com.therxmv.dirolreader.data.source.remote.media.MediaSource
import com.therxmv.dirolreader.data.source.remote.message.MessageRemoteDataSource
import com.therxmv.dirolreader.data.source.remote.message.MessageSource
import com.therxmv.dirolreader.data.source.remote.translation.TranslationRemoteSource
import com.therxmv.dirolreader.data.source.remote.translation.TranslationSource
import com.therxmv.dirolreader.data.source.remote.user.UserRemoteDataSource
import com.therxmv.dirolreader.data.source.remote.user.UserSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindsMessageRemoteDataSource(source: MessageRemoteDataSource): MessageSource

    @Binds
    @Singleton
    abstract fun bindsUserRemoteDataSource(source: UserRemoteDataSource): UserSource

    @Binds
    @Singleton
    abstract fun bindsMediaRemoteDataSource(source: MediaRemoteDataSource): MediaSource

    @Binds
    @Singleton
    abstract fun bindsTranslationRemoteDataSource(source: TranslationRemoteSource): TranslationSource
}
