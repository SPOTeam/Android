package com.example.spoteam_android.data.dummy.di

import com.example.spoteam_android.data.dummy.datasource.DummyDataSource
import com.example.spoteam_android.data.dummy.datasourceimpl.DummyDataSourceImpl
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
    abstract fun bindDummyDataSource(
        impl: DummyDataSourceImpl
    ): DummyDataSource
}