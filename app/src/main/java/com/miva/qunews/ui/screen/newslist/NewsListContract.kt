package com.miva.qunews.ui.screen.newslist

import com.miva.qunews.domain.model.NewsArticle

object NewsListContract {
    // State
    data class State(
        val articles: List<NewsArticle> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isRefreshing: Boolean = false
    )

    // Events (User Interactions)
    sealed class Event {
        object LoadNews : Event()
        object Refresh : Event()
        data class OnArticleClick(val article: NewsArticle) : Event()
        data class OnSaveArticle(val article: NewsArticle) : Event()
    }

    // Effects (One-time events)
    sealed class Effect {
        data class NavigateToDetail(val articleUrl: String) : Effect()
        data class ShowError(val message: String) : Effect()
        data class ShowSnackbar(val message: String) : Effect()
    }
}