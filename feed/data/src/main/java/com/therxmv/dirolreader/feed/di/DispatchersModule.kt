package com.therxmv.dirolreader.feed.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DispatchersModule {

    @Singleton
    @Provides
    @Named("IO")
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}