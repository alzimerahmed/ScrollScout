package com.therxmv.dirolreader.di

import com.therxmv.dirolreader.BuildConfig
import com.therxmv.dirolreader.channels.di.LocalDataSourceModule
import com.therxmv.dirolreader.channels.di.RemoteDataSourceModule
import com.therxmv.dirolreader.channels.di.RepositoryModule
import com.therxmv.dirolreader.feed.di.ClientModule
import com.therxmv.dirolreader.feed.di.DispatchersModule
import com.therxmv.dirolreader.feed.di.RemoteDataSourceModule as FeedRemoteDataSourceModule
import com.therxmv.dirolreader.feed.di.RepositoryModule as FeedRepositoryModule
import com.therxmv.otaupdates.data.di.OtaModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        RemoteDataSourceModule::class,
        LocalDataSourceModule::class,
        RepositoryModule::class,
        FeedRemoteDataSourceModule::class,
        FeedRepositoryModule::class,
        DispatchersModule::class,
        ClientModule::class,
        OtaModule::class,
    ]
)
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    @Named("VersionCode")
    fun providesVersionCode(): Int = BuildConfig.VERSION_CODE
}