package com.miva.qunews.ui.screen.newsdetail

import com.miva.qunews.domain.model.NewsArticle

object NewsDetailContract {
    data class State(
        val article: NewsArticle? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Event {
        data class LoadArticle(val url: String) : Event()
        object OnBackClick : Event()
        object OnSaveClick : Event()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowSnackbar(val message: String) : Effect()
    }
}