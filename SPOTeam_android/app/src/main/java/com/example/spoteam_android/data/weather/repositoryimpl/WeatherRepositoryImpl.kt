package com.example.spoteam_android.data.weather.repositoryimpl

import com.example.spoteam_android.data.weather.datasource.WeatherDataSource
import com.example.spoteam_android.data.weather.mapper.WeatherMapper
import com.example.spoteam_android.domain.weather.repository.WeatherRepository
import com.example.spoteam_android.domain.weather.entity.WeatherUiState
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val dataSource: WeatherDataSource
) : WeatherRepository {
    override suspend fun getTodayWeather(): Result<WeatherUiState> = runCatching {
        val today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        val dto = dataSource.getWeather("JSON", 100, 1, today, "0500", "60", "127")
        WeatherMapper.toUiState(dto)
    }
}
