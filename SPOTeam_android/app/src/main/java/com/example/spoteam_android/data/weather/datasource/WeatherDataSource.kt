package com.example.spoteam_android.data.weather.datasource

import com.example.spoteam_android.data.weather.dto.WeatherDto

interface WeatherDataSource {
    suspend fun getWeather(
        dataType: String,
        numOfRows: Int,
        pageNo: Int,
        baseDate: Int,
        baseTime: String,
        nx: String,
        ny: String
    ): WeatherDto
}