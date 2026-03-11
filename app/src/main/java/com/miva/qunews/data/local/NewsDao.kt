package com.miva.qunews.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.miva.qunews.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_articles ORDER BY timestamp DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news_articles WHERE url = :url")
    suspend fun getArticleByUrl(url: String): NewsEntity?

    @Query("SELECT * FROM news_articles WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchNews(query: String): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: NewsEntity)

    @Delete
    suspend fun deleteArticle(article: NewsEntity)

    @Query("DELETE FROM news_articles")
    suspend fun clearAll()
}