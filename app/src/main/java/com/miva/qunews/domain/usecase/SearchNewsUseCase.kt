package com.miva.qunews.domain.usecase

import com.miva.qunews.ApiNewsResult
import com.miva.qunews.domain.model.NewsArticle
import com.miva.qunews.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(query: String): Flow<ApiNewsResult<List<NewsArticle>>> {
        return repository.searchNews(query)
    }
}