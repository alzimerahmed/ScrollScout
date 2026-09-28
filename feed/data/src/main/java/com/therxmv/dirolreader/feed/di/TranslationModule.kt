package com.therxmv.dirolreader.feed.di

import com.therxmv.dirolreader.data.source.remote.translation.TranslationApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TranslationModule {

    @Provides
    @Singleton
    fun provideTranslationApi(): TranslationApi =
        Retrofit.Builder()
            .baseUrl("https://translate.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApi::class.java)
}
