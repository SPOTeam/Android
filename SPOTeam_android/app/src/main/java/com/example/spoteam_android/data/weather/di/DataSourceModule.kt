package com.example.spoteam_android.data.weather.di

import com.example.spoteam_android.data.weather.datasource.WeatherDataSource
import com.example.spoteam_android.data.weather.datasourceimpl.WeatherDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindWeatherDataSource(
        impl: WeatherDataSourceImpl
    ): WeatherDataSource
}