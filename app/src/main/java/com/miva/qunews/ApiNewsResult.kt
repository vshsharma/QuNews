package com.miva.qunews

import com.miva.qunews.domain.model.NewsArticle

sealed interface ApiNewsResult<out T> {
    data class Loading<out T>(val data: T? = null) : ApiNewsResult<T>

    data class Success<out T>(val data: T) : ApiNewsResult<T>

    data class Error<out T>(val message: String, val data: T? = null) : ApiNewsResult<T>
}
