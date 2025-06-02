package com.example.spoteam_android.data.study.di

import com.example.spoteam_android.data.study.service.StudyService
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
    fun providesStudyService(retrofit: Retrofit): StudyService =
        retrofit.create(StudyService::class.java)
}