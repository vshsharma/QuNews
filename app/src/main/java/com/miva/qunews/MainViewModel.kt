package com.miva.qunews

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miva.qunews.domain.usecase.NewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getNewsUseCase: NewsUseCase,
): ViewModel() {

    fun loadNews() {
        viewModelScope.launch {
            getNewsUseCase(true).collectLatest { result ->
                println("Vishal: $result")
            }
        }
    }
}