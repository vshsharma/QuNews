package com.miva.qunews.ui.screen.search

import com.miva.qunews.domain.model.NewsArticle

object SearchContract {
    data class State(
        val query: String = "",
        val articles: List<NewsArticle> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Event {
        data class OnQueryChange(val query: String) : Event()
        object OnSearch : Event()
        data class OnArticleClick(val article: NewsArticle) : Event()
        object OnClearQuery : Event()
    }

    sealed class Effect {
        data class NavigateToDetail(val articleUrl: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
