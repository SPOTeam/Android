package com.umcspot.android.weather

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

    //인자 값을 기준으로 repository의 getWeather 메소드를 실행
    fun getWeather(
        dataType: String, numOfRows: Int, pageNo: Int,
        baseDate: Int, baseTime: String, nx: String, ny: String
    ) {
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