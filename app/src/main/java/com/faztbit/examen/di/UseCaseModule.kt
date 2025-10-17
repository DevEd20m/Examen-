package com.faztbit.examen.di

import com.faztbit.examen.domain.usecase.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindGetMedalsUseCase(
        getMedalsUseCaseImpl: GetMedalsUseCaseImpl
    ): GetMedalsUseCase

    @Binds
    @Singleton
    abstract fun bindUpdateMedalPointsUseCase(
        updateMedalPointsUseCaseImpl: UpdateMedalPointsUseCaseImpl
    ): UpdateMedalPointsUseCase

    @Binds
    @Singleton
    abstract fun bindResetAllProgressUseCase(
        resetAllProgressUseCaseImpl: ResetAllProgressUseCaseImpl
    ): ResetAllProgressUseCase

    @Binds
    @Singleton
    abstract fun bindHandleAvatarTapUseCase(
        handleAvatarTapUseCaseImpl: HandleAvatarTapUseCaseImpl
    ): HandleAvatarTapUseCase
}