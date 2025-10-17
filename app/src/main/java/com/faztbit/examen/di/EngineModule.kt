package com.faztbit.examen.di

import com.faztbit.examen.domain.engine.PointsEngine
import com.faztbit.examen.domain.repository.MedalRepository
import com.faztbit.examen.domain.usecase.GetMedalsUseCase
import com.faztbit.examen.domain.usecase.UpdateMedalPointsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun providePointsEngine(
        medalRepository: MedalRepository,
        getMedalsUseCase: GetMedalsUseCase,
        updateMedalPointsUseCase: UpdateMedalPointsUseCase
    ): PointsEngine = PointsEngine(medalRepository, getMedalsUseCase, updateMedalPointsUseCase)
}