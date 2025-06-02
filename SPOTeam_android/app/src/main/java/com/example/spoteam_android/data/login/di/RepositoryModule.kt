package com.example.spoteam_android.data.login.di

import com.example.spoteam_android.data.login.repositoryimpl.LoginRepositoryImpl
import com.example.spoteam_android.data.login.repositoryimpl.SocialLoginRepositoryImpl
import com.example.spoteam_android.domain.login.repository.LoginRepository
import com.example.spoteam_android.domain.login.repository.SocialLoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindsSocialLoginRepository(
        socialLoginRepositoryImpl: SocialLoginRepositoryImpl
    ): SocialLoginRepository
}
