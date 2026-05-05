package com.miva.qunews.ui.screen.newslist

import app.cash.turbine.test
import com.miva.qunews.ApiNewsResult
import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.usecase.NewsUseCase
import com.miva.qunews.domain.usecase.SaveArticleUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsListViewModelTest {

    @MockK
    private lateinit var newsUseCase: NewsUseCase

    @MockK
    private lateinit var saveArticleUseCase: SaveArticleUseCase

    private lateinit var viewModel: NewsListViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val dummyArticle = NewsArticle(
        url = "url1",
        title = "title1",
        description = "desc1",
        urlToImage = "",
        publishedAt = "",
        content = "",
        author = "",
        sourceName = "",
        isSaved = false
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        coEvery { newsUseCase(any()) } returns flowOf(ApiNewsResult.Loading())
        viewModel = NewsListViewModel(newsUseCase, saveArticleUseCase)
    }

    @Test
    fun loadNews_emits_articles_on_success() = runTest {
        val articles = listOf(dummyArticle)
        coEvery { newsUseCase(true) } returns flowOf(ApiNewsResult.Success(articles))

        viewModel.handleEvent(NewsListContract.Event.LoadNews)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val loadedState = awaitItem()
            assertEquals(false, loadedState.isLoading)
            assertEquals(articles, loadedState.articles)
        }
    }

    @Test
    fun `loadNews emits error`() = runTest{
        coEvery { newsUseCase(true) } returns flowOf(ApiNewsResult.Error("Something went wrong"))

        viewModel.handleEvent(NewsListContract.Event.LoadNews)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val result = awaitItem()
            assertEquals("Something went wrong", result.error)
        }
    }

    @Test
    fun `loadNews emits error_1`() = runTest{
        coEvery { newsUseCase(true) } returns flowOf(ApiNewsResult.Error("Something went wrong"))

        viewModel.handleEvent(NewsListContract.Event.LoadNews)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val result = awaitItem()
            assertEquals("Something went wrong", result.error)
        }
    }

}