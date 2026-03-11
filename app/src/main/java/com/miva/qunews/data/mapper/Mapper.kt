package com.miva.qunews.data.mapper

import com.miva.qunews.data.entity.ArticleDto
import com.miva.qunews.data.local.entity.NewsEntity
import com.miva.qunews.domain.model.NewsArticle

private fun ArticleDto.toNewsEntity(): NewsEntity {
    return NewsEntity(
        url = url,
        sourceName = source?.name ?: "Unknown",
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

private fun NewsEntity.toNewsArticle(): NewsArticle {
    return NewsArticle(
        url = url,
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        isSaved = isSaved
    )
}

private fun NewsArticle.toNewsEntity(): NewsEntity {
    return NewsEntity(
        url = url,
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        isSaved = isSaved
    )
}