package com.therxmv.dirolreader.channels.di

import com.therxmv.dirolreader.channels.data.repository.ChannelRepositoryImpl
import com.therxmv.dirolreader.channels.domain.repository.ChannelRepository
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
    abstract fun bindsChannelRepository(repository: ChannelRepositoryImpl): ChannelRepository
}
