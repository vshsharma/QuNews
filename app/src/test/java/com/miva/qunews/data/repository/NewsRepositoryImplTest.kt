package com.miva.qunews.data.repository

import com.miva.qunews.ApiNewsResult
import com.miva.qunews.BuildConfig
import com.miva.qunews.data.api.NewsApi
import com.miva.qunews.data.remote.dto.ArticleDto
import com.miva.qunews.data.remote.dto.NewsResponse
import com.miva.qunews.data.remote.dto.SourceDto
import com.miva.qunews.data.local.NewsDao
import com.miva.qunews.data.local.entity.NewsEntity
import com.miva.qunews.domain.model.NewsArticle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryTest {

    private val newsApi: NewsApi = mockk()
    private val dao: NewsDao = mockk()

    private val apiKey = BuildConfig.NEWS_API_KEY
    private lateinit var newsRepository: NewsRepositoryImpl

    @BeforeEach
    fun setUp() {
        newsRepository = NewsRepositoryImpl(newsApi, dao, apiKey)
    }



    @Test
    fun `getNews returns cached data when available and fetchFromRemote is false`() = runTest {
        // Arrange
        val cachedEntities = listOf(
            NewsEntity(
                "url1", "title1", "desc1", title = "titel 1", isSaved = true,
                description = "Description",
                urlToImage = "",
                publishedAt = "",
                content = "Content",
                timestamp = 10000,
            )
        )
        coEvery { dao.getAllNews() } returns flowOf(cachedEntities)

        // Act
        val result = newsRepository.getNews(fetchFromRemote = false)
        val emissions = mutableListOf<ApiNewsResult<List<NewsArticle>>>()
        result.collect { emissions.add(it) }

        // Assert
        assertTrue(emissions.any { it is ApiNewsResult.Success })
        val errorEmission = emissions.find { it is ApiNewsResult.Success } as? ApiNewsResult.Success
        assertTrue(errorEmission?.data?.size == 1)
    }

    @Test
    fun `getNews fetches from remote and updates cache when fetchFromRemote is true`() = runTest {
        // Arrange
        val cachedEntities = emptyList<NewsEntity>()
        val remoteArticles = NewsResponse(
            status = "ok",
            totalResults = 2,
            articles = articles()
        )
        coEvery { dao.getAllNews() } returns flowOf(cachedEntities)
        coEvery { newsApi.getTopHeadlines(country = "us", apiKey = apiKey, page = 1, pageSize = 100) } returns remoteArticles
        coEvery { dao.clearAll() } returns Unit
        coEvery { dao.insertArticles(any()) } returns Unit

        // Act
        val result = newsRepository.getNews(fetchFromRemote = true)
        val emissions = mutableListOf<ApiNewsResult<List<NewsArticle>>>()
        result.collect { emissions.add(it) }

        // Assert
        coVerify { newsApi.getTopHeadlines(country = "us", apiKey = apiKey, page = 1, pageSize = 100) }
        coVerify { dao.clearAll() }
        coVerify { dao.insertArticles(any()) }
    }

    @Test
    fun `getNews throws http exception when fetching from remote `() = runTest {
        // Arrange
        val cachedEntities = emptyList<NewsEntity>()
        val errorResponse = Response.error<Any>(404, "Not Found".toResponseBody(null))
        val httpException = HttpException(errorResponse)

        coEvery { dao.getAllNews() } returns flowOf(cachedEntities)
        coEvery { newsApi.getTopHeadlines(country = "us", apiKey = apiKey, page = 1, pageSize = 100) } throws httpException
        coEvery { dao.clearAll() } returns Unit
        coEvery { dao.insertArticles(any()) } returns Unit

        // Act
        val result = newsRepository.getNews(fetchFromRemote = true)
        val emissions = mutableListOf<ApiNewsResult<List<NewsArticle>>>()
        result.collect { emissions.add(it) }

        val errorEmission = emissions.find { it is ApiNewsResult.Error } as? ApiNewsResult.Error
        assertEquals("Oops, something went wrong!", errorEmission?.message)
    }

    @Test
    fun `getNews throws io exception when fetching from remote `() = runTest {
        // Arrange
        val cachedEntities = emptyList<NewsEntity>()

        val ioException = IOException()

        coEvery { dao.getAllNews() } returns flowOf(cachedEntities)
        coEvery { newsApi.getTopHeadlines(country = "us", apiKey = apiKey, page = 1, pageSize = 100) } throws ioException
        coEvery { dao.clearAll() } returns Unit
        coEvery { dao.insertArticles(any()) } returns Unit

        // Act
        val result = newsRepository.getNews(fetchFromRemote = true)
        val emissions = mutableListOf<ApiNewsResult<List<NewsArticle>>>()
        result.collect { emissions.add(it) }

        val errorEmission = emissions.find { it is ApiNewsResult.Error } as? ApiNewsResult.Error
        assertEquals("Couldn't reach server, check your internet connection.", errorEmission?.message)
    }

    private fun articles(): List<ArticleDto> = listOf(
        ArticleDto(
            author = "John Doe",
            title = "Breaking News: Kotlin is Awesome",
            description = "Kotlin is gaining popularity among developers.",
            url = "https://example.com/kotlin-news",
            urlToImage = "https://example.com/images/kotlin.png",
            publishedAt = "2026-03-12T10:00:00Z",
            content = "Kotlin is now one of the top languages for Android development.",
            source = SourceDto("1", "Hello"),
        ),
        ArticleDto(
            author = "Jane Smith",
            title = "Tech Trends 2026",
            description = "A look at the top tech trends for 2026.",
            url = "https://example.com/tech-trends",
            urlToImage = "https://example.com/images/tech.png",
            publishedAt = "2026-03-11T09:30:00Z",
            content = "AI, IoT, and Kotlin are leading the way in 2026.",
            source = SourceDto("2", "Hello 2"),
        )
    )
}