package com.example.spoteam_android.domain.weather.repository

import com.example.spoteam_android.domain.weather.entity.WeatherUiState

interface WeatherRepository {
    suspend fun getTodayWeather(): Result<WeatherUiState>
}

