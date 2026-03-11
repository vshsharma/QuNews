package com.miva.qunews.ui.screen.newslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miva.qunews.ApiNewsResult
import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.usecase.NewsUseCase
import com.miva.qunews.domain.usecase.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getNewsUseCase: NewsUseCase,
    private val saveArticleUseCase: SaveArticleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsListContract.State())
    val state: StateFlow<NewsListContract.State> = _state.asStateFlow()

    private val _effect = Channel<NewsListContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleEvent(NewsListContract.Event.LoadNews)
    }

    fun handleEvent(event: NewsListContract.Event) {
        when (event) {
            is NewsListContract.Event.LoadNews -> loadNews(false)
            is NewsListContract.Event.Refresh -> loadNews(true)
            is NewsListContract.Event.OnArticleClick -> navigateToDetail(event.article)
            is NewsListContract.Event.OnSaveArticle -> saveArticle(event.article)
        }
    }

    private fun loadNews(fetchFromRemote: Boolean) {
        viewModelScope.launch {
            getNewsUseCase(fetchFromRemote).collectLatest { result ->
                when (result) {
                    is ApiNewsResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = result.data.isNullOrEmpty(),
                                isRefreshing = fetchFromRemote && !result.data.isNullOrEmpty(),
                                articles = result.data ?: emptyList()
                            )
                        }
                    }
                    is ApiNewsResult.Success -> {
                        _state.update {
                            it.copy(
                                articles = result.data ?: emptyList(),
                                isLoading = false,
                                isRefreshing = false,
                                error = null
                            )
                        }
                    }
                    is ApiNewsResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = result.message
                            )
                        }
                        _effect.send(NewsListContract.Effect.ShowError(result.message ?: "Unknown error"))
                    }
                }
            }
        }
    }

    private fun navigateToDetail(article: NewsArticle) {
        viewModelScope.launch {
            _effect.send(NewsListContract.Effect.NavigateToDetail(article.url))
        }
    }

    private fun saveArticle(article: NewsArticle) {
        viewModelScope.launch {
            saveArticleUseCase(article)
            _effect.send(NewsListContract.Effect.ShowSnackbar("Article saved"))
        }
    }
}