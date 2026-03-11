package com.miva.qunews.domain.repository

import androidx.compose.ui.text.LinkAnnotation
import com.miva.qunews.ApiNewsResult
import com.miva.qunews.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(fetchFromRemote: Boolean): Flow<ApiNewsResult<List<NewsArticle>>>
    suspend fun saveArticle(article: NewsArticle)
    suspend fun getArticleByUrl(url: String): NewsArticle?
    fun searchNews(query: String): Flow<ApiNewsResult<List<NewsArticle>>>
}