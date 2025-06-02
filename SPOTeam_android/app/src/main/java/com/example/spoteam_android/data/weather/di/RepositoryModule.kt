package com.example.spoteam_android.data.weather.di

import com.example.spoteam_android.data.weather.repositoryimpl.WeatherRepositoryImpl
import com.example.spoteam_android.domain.weather.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository
}