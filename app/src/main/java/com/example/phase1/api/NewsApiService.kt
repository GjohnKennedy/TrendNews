package com.example.phase1.api

import com.example.phase1.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v4/articles")
    suspend fun getSpaceflightNews(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<NewsResponse>


    @GET("v4/articles")
    suspend fun searchSpaceflightNews(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("title_contains_one") query: String
    ): Response<NewsResponse>
}