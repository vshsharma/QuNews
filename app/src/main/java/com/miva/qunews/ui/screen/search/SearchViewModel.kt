package com.miva.qunews.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miva.qunews.ApiNewsResult
import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.usecase.SearchNewsUseCase
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
class SearchViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchContract.State())
    val state: StateFlow<SearchContract.State> = _state.asStateFlow()

    private val _effect = Channel<SearchContract.Effect>()
    val effect = _effect.receiveAsFlow()

    fun handleEvent(event: SearchContract.Event) {
        when (event) {
            is SearchContract.Event.OnQueryChange -> {
                _state.update { it.copy(query = event.query) }
            }
            is SearchContract.Event.OnSearch -> searchNews()
            is SearchContract.Event.OnArticleClick -> navigateToDetail(event.article)
            is SearchContract.Event.OnClearQuery -> {
                _state.update { it.copy(query = "", articles = emptyList()) }
            }
        }
    }

    private fun searchNews() {
        val query = _state.value.query
        if (query.isBlank()) return

        viewModelScope.launch {
            searchNewsUseCase(query).collectLatest { result ->
                when (result) {
                    is ApiNewsResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                articles = result.data ?: emptyList()
                            )
                        }
                    }
                    is ApiNewsResult.Success -> {
                        _state.update {
                            it.copy(
                                articles = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is ApiNewsResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        _effect.send(
                            SearchContract.Effect.ShowError(result.message ?: "Unknown error")
                        )
                    }
                }
            }
        }
    }

    private fun navigateToDetail(article: NewsArticle) {
        viewModelScope.launch {
            _effect.send(SearchContract.Effect.NavigateToDetail(article.url))
        }
    }
}