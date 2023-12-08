package com.example.phase1.model

import com.google.gson.annotations.SerializedName


data class NewsResponse(
    val count: Long,
    val next: String,
    val previous: Any?,
    val results: List<Article>,
)

data class Article(
    val id: Long,
    val title: String,
    val url: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("news_site")
    val newsSite: String,
    val summary: String,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val featured: Boolean,
    val launches: List<Launch>,
    val events: List<Any?>,
)

data class Launch(
    @SerializedName("launch_id")
    val launchId: String,
    val provider: String,
)
