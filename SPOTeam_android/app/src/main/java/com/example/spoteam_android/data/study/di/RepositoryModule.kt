package com.example.spoteam_android.data.study.di

import com.example.spoteam_android.data.study.repositoryimpl.StudyRepositoryImpl
import com.example.spoteam_android.domain.study.repository.StudyRepository
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
    abstract fun bindsStudyRepository(
        studyRepositoryImpl: StudyRepositoryImpl
    ): StudyRepository
}