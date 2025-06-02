package com.example.spoteam_android.data.weather.service

import retrofit2.http.Query
import com.example.spoteam_android.core.util.ApiKey.Companion.API_KEY
import com.example.spoteam_android.data.weather.dto.WeatherDto
import retrofit2.Response
import retrofit2.http.GET

interface WeatherService {
    @GET("getVilageFcst?serviceKey=$API_KEY")
    suspend fun getWeather(
        @Query("dataType") dataType: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("base_date") baseDate: Int,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: String,
        @Query("ny") ny: String
    ): Response<WeatherDto>
}