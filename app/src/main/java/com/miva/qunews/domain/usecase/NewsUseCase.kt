package com.miva.qunews.domain.usecase

import com.miva.qunews.ApiNewsResult
import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(fetchFromRemote: Boolean = true): Flow<ApiNewsResult<List<NewsArticle>>> {
        return repository.getNews(fetchFromRemote)
    }
}