package com.example.phase1.weather.viewmoel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phase1.weather.model.WeatherResponse
import com.example.phase1.weather.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {


    val loading = MutableLiveData<Boolean>()

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getWeatherData(latitude: Double, longitude: Double) {
        loading.postValue(true)

        viewModelScope.launch {
            try {
                val response = repository.getWeatherData(latitude, longitude)
                _weatherData.postValue(response)
                loading.postValue(false)

            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching weather data")
                loading.postValue(false)

            }
        }
    }
}
