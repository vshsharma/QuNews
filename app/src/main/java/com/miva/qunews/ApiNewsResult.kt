package com.miva.qunews

sealed interface ApiNewsResult<out T> {
    data class Loading<out T>(val data: T? = null) : ApiNewsResult<T>

    data class Success<out T>(val data: T) : ApiNewsResult<T>

    data class Error<out T>(val message: String, val data: T? = null) : ApiNewsResult<T>
}
