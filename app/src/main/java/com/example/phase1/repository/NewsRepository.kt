package com.example.phase1.repository

import com.example.phase1.api.NewsApiService
import com.example.phase1.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject



class NewsRepository @Inject constructor(private val api: NewsApiService) {

    suspend fun getSpaceflightNews(limit: Int, offset: Int): Response<NewsResponse> {
        return api.getSpaceflightNews(limit = limit, offset = offset)
    }

    suspend fun searchSpaceflightNews(query: String, limit: Int, offset: Int): Response<NewsResponse> {
        return api.searchSpaceflightNews(query = query, limit = limit, offset = offset)
    }
}
