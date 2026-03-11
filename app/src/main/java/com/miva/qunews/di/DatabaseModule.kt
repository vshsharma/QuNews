package com.miva.qunews.di

import android.app.Application
import androidx.room.Room
import com.miva.qunews.data.api.NewsApi
import com.miva.qunews.data.local.NewsDao
import com.miva.qunews.data.local.NewsDatabase
import com.miva.qunews.data.repository.NewsRepositoryImpl
import com.miva.qunews.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(app: Application): NewsDatabase {
        return Room.databaseBuilder(
            app,
            NewsDatabase::class.java,
            "news_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNewsDao(database: NewsDatabase): NewsDao {
        return database.newsDao
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        api: NewsApi,
        dao: NewsDao,
        @Named("api_key") apiKey: String
    ): NewsRepository {
        return NewsRepositoryImpl(api, dao, apiKey)
    }
}