package com.example.spoteam_android.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherResponse : MutableLiveData<Response<Weather>> = MutableLiveData()
    val weatherResponse get() = _weatherResponse

    fun getWeather(
        dataType: String, numOfRows: Int, pageNo: Int,
        baseDate: Int, baseTime: String, nx: String, ny: String
    ) {
        Log.d(
            "WeatherRequest",
            "Requesting weather data with params: dataType=$dataType, numOfRows=$numOfRows, pageNo=$pageNo, baseDate=$baseDate, baseTime=$baseTime, nx=$nx, ny=$ny"
        )

        viewModelScope.launch {
            try {
                val response =
                    repository.getWeather(dataType, numOfRows, pageNo, baseDate, baseTime, nx, ny)
                _weatherResponse.value = response
                Log.d("WeatherResponse", "Response received: $response")
            } catch (e: Exception) {
                Log.e("WeatherError", "Failed to fetch weather data", e)
            }
        }
    }
}