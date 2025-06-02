package com.example.spoteam_android.data.dummy.di

import com.example.spoteam_android.data.dummy.repositoryimpl.DummyRepositoryImpl
import com.example.spoteam_android.domain.dummy.repository.DummyRepository
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
    abstract fun bindsDummyRepository(
        dummyRepositoryImpl: DummyRepositoryImpl
    ): DummyRepository
}