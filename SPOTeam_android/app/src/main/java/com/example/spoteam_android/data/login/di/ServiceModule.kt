package com.example.spoteam_android.data.login.di

import com.example.spoteam_android.data.login.service.LoginService
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
    fun providesLoginService(retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)
}