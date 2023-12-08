package com.example.phase1.weather.repository

import com.example.phase1.weather.api.WeatherApiService
import com.example.phase1.weather.model.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiService: WeatherApiService) {

    suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherResponse {
        return apiService.getWeatherData(latitude, longitude)
    }
}
