package com.example.spoteam_android.di

import com.example.spoteam_android.data.login.service.LoginService
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.interestarea.InterestAreaApiService
import com.example.spoteam_android.presentation.interestarea.RecommendStudyApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    // TODO: 나중에 패키지 위치 이동
    @Provides
    @Singleton
    fun provideInterestAreaApiService(retrofit: Retrofit): InterestAreaApiService =
        retrofit.create(InterestAreaApiService::class.java)

    // TODO: 나중에 패키지 위치 이동
    @Provides
    @Singleton
    fun provideRecommendStudyApiService(retrofit: Retrofit): RecommendStudyApiService =
        retrofit.create(RecommendStudyApiService::class.java)
    // TODO: 나중에 패키지 위치 이동
    @Provides
    @Singleton
    fun provideCommunityApiService(retrofit: Retrofit): CommunityAPIService =
        retrofit.create(CommunityAPIService::class.java)


}