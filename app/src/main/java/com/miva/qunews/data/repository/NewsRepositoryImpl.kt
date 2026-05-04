package com.miva.qunews.data.repository

import com.miva.qunews.ApiNewsResult
import com.miva.qunews.data.api.NewsApi
import com.miva.qunews.data.local.NewsDao
import com.miva.qunews.data.mapper.toNewsArticle
import com.miva.qunews.data.mapper.toNewsEntity
import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class NewsRepositoryImpl(
    private val api: NewsApi,
    private val dao: NewsDao,
    private val apiKey: String
): NewsRepository {

    override fun getNews(fetchFromRemote: Boolean): Flow<ApiNewsResult<List<NewsArticle>>> = flow {
        emit(ApiNewsResult.Loading())

        val localArticles = dao.getAllNews().first()
        val cachedData = localArticles.map { it.toNewsArticle() }

        if (cachedData.isNotEmpty()) {
            emit(ApiNewsResult.Loading(data = cachedData))
            println("Vishal cached data: $cachedData")
        }

        println("Vishal fetchFromRemote: $fetchFromRemote")
        if (fetchFromRemote) {
            try {
                val remoteArticles = api.getTopHeadlines(apiKey = apiKey)
                dao.clearAll()
                dao.insertArticles(
                    remoteArticles.articles.map { it.toNewsEntity() })
            } catch (_: HttpException) {
                emit(
                    ApiNewsResult.Error(
                        message = "Oops, something went wrong!",
                    data = dao.getAllNews().map { entities ->
                        entities.map { it.toNewsArticle() }
                    }.let { null }))
            } catch (_: IOException) {
                emit(
                    ApiNewsResult.Error(
                        message = "Couldn't reach server, check your internet connection.",
                    data = dao.getAllNews().map { entities ->
                        entities.map { it.toNewsArticle() }
                    }.let { null }))
            }
        } else if (cachedData.isEmpty()) {
            emit(
                ApiNewsResult.Error(
                message = "No data found, check your internet connection."))
        }
        // Emit fresh data from database
        dao.getAllNews().collect { entities ->
            emit(ApiNewsResult.Success(entities.map { it.toNewsArticle() }))
        }
    }

    override fun searchNews(query: String): Flow<ApiNewsResult<List<NewsArticle>>> = flow {
        emit(ApiNewsResult.Loading())
        try {
            // Search locally first
            val localResults = dao.searchNews(query).first()
            println("Vishal local results: $localResults")
            if (localResults.isNotEmpty()) {
                emit(ApiNewsResult.Loading(data = localResults.map { it.toNewsArticle() }))
            }
            println("Vishal local results: from remote")


            // Then fetch from remote
            val remoteResults = api.searchNews(query = query, apiKey = apiKey)
            val articles = remoteResults.articles.map { it.toNewsEntity() }
            dao.insertArticles(articles)

            emit(ApiNewsResult.Success(articles.map { it.toNewsArticle() }))
        } catch (e: HttpException) {
            emit(ApiNewsResult.Error(message = "Oops, something went wrong!"))
        } catch (e: IOException) {
            emit(ApiNewsResult.Error(message = "Couldn't reach server, check your internet connection."))
        }
    }

    override suspend fun saveArticle(article: NewsArticle) {
        dao.insertArticle(article.toNewsEntity().copy(isSaved = true))
    }

    override suspend fun getArticleByUrl(url: String) : NewsArticle?{
        return dao.getArticleByUrl(url)?.toNewsArticle()
    }
}