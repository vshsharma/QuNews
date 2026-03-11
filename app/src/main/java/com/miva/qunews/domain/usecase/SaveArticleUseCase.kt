package com.miva.qunews.domain.usecase

import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.repository.NewsRepository
import javax.inject.Inject

class SaveArticleUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(article: NewsArticle) {
        repository.saveArticle(article)
    }
}