package com.faztbit.examen.di

import com.faztbit.examen.data.repository.MedalRepositoryImpl
import com.faztbit.examen.domain.repository.MedalRepository
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
    abstract fun bindMedalRepository(
        medalRepositoryImpl: MedalRepositoryImpl
    ): MedalRepository
}