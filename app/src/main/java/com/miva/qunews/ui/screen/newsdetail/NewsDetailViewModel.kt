package com.miva.qunews.ui.screen.newsdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miva.qunews.domain.usecase.GetArticleByIdUseCase
import com.miva.qunews.domain.usecase.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val getArticleByIdUseCase: GetArticleByIdUseCase,
    private val saveArticleUseCase: SaveArticleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsDetailContract.State())
    val state: StateFlow<NewsDetailContract.State> = _state.asStateFlow()

    private val _effect = Channel<NewsDetailContract.Effect>()
    val effect = _effect.receiveAsFlow()

    fun handleEvent(event: NewsDetailContract.Event) {
        when (event) {
            is NewsDetailContract.Event.LoadArticle -> loadArticle(event.url)
            is NewsDetailContract.Event.OnBackClick -> navigateBack()
            is NewsDetailContract.Event.OnSaveClick -> saveArticle()
        }
    }

    private fun loadArticle(url: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val article = getArticleByIdUseCase(url)
                _state.update {
                    it.copy(
                        article = article,
                        isLoading = false,
                        error = if (article == null) "Article not found" else null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.send(NewsDetailContract.Effect.NavigateBack)
        }
    }

    private fun saveArticle() {
        viewModelScope.launch {
            _state.value.article?.let { article ->
                saveArticleUseCase(article)
                _effect.send(NewsDetailContract.Effect.ShowSnackbar("Article saved"))
            }
        }
    }
}