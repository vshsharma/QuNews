package com.miva.qunews.data.mapper

import com.miva.qunews.data.remote.dto.ArticleDto
import com.miva.qunews.data.local.entity.NewsEntity
import com.miva.qunews.domain.model.NewsArticle

 fun ArticleDto.toNewsEntity(): NewsEntity {
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

 fun NewsEntity.toNewsArticle(): NewsArticle {
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

 fun NewsArticle.toNewsEntity(): NewsEntity {
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