package com.example.phase1.weather.api

import com.example.phase1.weather.model.WeatherResponse
import com.example.phase1.weather.utils.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("forecast")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") unit: String ="imperial"
    ): WeatherResponse
}
