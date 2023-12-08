package com.example.phase1.weather.module

import com.example.phase1.weather.api.WeatherApiService
import com.example.phase1.weather.utils.BASE_URLWEATHER
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleApi
{

    @Provides
    @Singleton
    fun provideRetrofit(gson:Gson,client : OkHttpClient) : WeatherApiService =
        Retrofit.Builder().baseUrl(BASE_URLWEATHER).addConverterFactory(GsonConverterFactory.create(gson))
            .client(client).build().create(WeatherApiService::class.java)



}










