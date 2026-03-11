package com.miva.qunews.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "news_articles")
data class NewsEntity(
    @PrimaryKey
    val url: String,
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val isSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)