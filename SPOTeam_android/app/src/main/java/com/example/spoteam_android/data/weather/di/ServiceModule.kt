package com.example.spoteam_android.data.weather.di

import com.example.spoteam_android.data.weather.service.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }
}