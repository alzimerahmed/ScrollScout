package com.therxmv.dirolreader.channels.di

import com.therxmv.dirolreader.channels.data.source.remote.channel.ChannelRemoteDataSource
import com.therxmv.dirolreader.channels.data.source.remote.channel.ChannelRemoteSource
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
    abstract fun bindsChannelRemoteDataSource(source: ChannelRemoteDataSource): ChannelRemoteSource
}
