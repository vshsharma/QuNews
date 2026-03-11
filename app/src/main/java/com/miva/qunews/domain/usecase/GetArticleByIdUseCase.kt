package com.miva.qunews.domain.usecase

import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.repository.NewsRepository
import javax.inject.Inject

class GetArticleByIdUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(url: String): NewsArticle? {
        return repository.getArticleByUrl(url)
    }
}