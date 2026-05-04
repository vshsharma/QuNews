package com.miva.qunews.domain.model

import androidx.compose.runtime.Immutable

data class NewsArticle(
    val url: String,
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val isSaved: Boolean = false
)
