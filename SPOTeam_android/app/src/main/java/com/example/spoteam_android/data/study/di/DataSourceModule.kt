package com.example.spoteam_android.data.study.di

import com.example.spoteam_android.data.study.datasource.StudyDataSource
import com.example.spoteam_android.data.study.datasourceimpl.StudyDataSourceImpl
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
    abstract fun bindStudyDataSource(
        impl: StudyDataSourceImpl
    ): StudyDataSource
}