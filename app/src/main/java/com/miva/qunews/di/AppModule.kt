package com.miva.qunews.di

import com.miva.qunews.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("api_key")
    fun provideApiKey(): String {
        return BuildConfig.NEWS_API_KEY
    }

    @Provides
    @Singleton
    @Named("base_url")
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }
}