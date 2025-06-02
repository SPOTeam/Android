package com.example.spoteam_android.data.login.di

import com.example.spoteam_android.data.login.datasource.local.TokenDataSource
import com.example.spoteam_android.data.datasource.local.TokenDataSourceImpl
import com.example.spoteam_android.data.login.datasource.remote.LoginDataSource
import com.example.spoteam_android.data.login.datasource.remote.SocialLoginDataSource
import com.example.spoteam_android.data.login.datasourceimpl.remote.LoginDataSourceImpl
import com.example.spoteam_android.data.login.datasourceimpl.remote.SocialLoginDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindLoginDataSource(
        impl: LoginDataSourceImpl
    ): LoginDataSource

    @Binds
    @Singleton
    abstract fun bindSocialLoginDataSource(
        impl: SocialLoginDataSourceImpl
    ): SocialLoginDataSource

    @Binds
    @Singleton
    abstract fun bindTokenDataSource(
        impl: TokenDataSourceImpl
    ): TokenDataSource


}