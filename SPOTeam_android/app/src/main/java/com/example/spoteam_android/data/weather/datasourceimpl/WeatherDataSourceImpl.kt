package com.example.spoteam_android.data.weather.datasourceimpl

import com.example.spoteam_android.data.weather.datasource.WeatherDataSource
import com.example.spoteam_android.data.weather.dto.WeatherDto
import com.example.spoteam_android.data.weather.service.WeatherService
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val api: WeatherService
) : WeatherDataSource {
    override suspend fun getWeather(
        dataType: String,
        numOfRows: Int,
        pageNo: Int,
        baseDate: Int,
        baseTime: String,
        nx: String,
        ny: String
    ): WeatherDto {
        val response = api.getWeather(dataType, numOfRows, pageNo, baseDate, baseTime, nx, ny)
        return response.body() ?: throw Exception("날씨 데이터 없음")
    }
}