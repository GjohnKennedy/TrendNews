package com.example.phase1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.phase1.model.Article
import com.example.phase1.paging.NewsPagingSource
import com.example.phase1.paging.SearchNewsPagingSource
import com.example.phase1.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    private val currentQuery = MutableStateFlow("")

    val news: Flow<PagingData<Article>> = currentQuery.flatMapLatest { query ->
        Pager(PagingConfig(pageSize = 15)) {
            if (query.isBlank()) {
                NewsPagingSource(repository)
            } else {
                SearchNewsPagingSource(repository, query)
            }
        }.flow.cachedIn(viewModelScope)
    }

    fun searchNews(query: String) {
        currentQuery.value = query
    }
}
