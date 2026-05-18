package com.pokiepaws.mobile.di

import com.pokiepaws.mobile.data.repository.AnimalRepositoryImpl
import com.pokiepaws.mobile.data.repository.AppSettingsRepositoryImpl
import com.pokiepaws.mobile.data.repository.AuthRepositoryImpl
import com.pokiepaws.mobile.data.repository.ClinicRepositoryImpl
import com.pokiepaws.mobile.data.repository.NotificationRepositoryImpl
import com.pokiepaws.mobile.data.repository.VetRepositoryImpl
import com.pokiepaws.mobile.data.repository.VisitRepositoryImpl
import com.pokiepaws.mobile.domain.repository.AnimalRepository
import com.pokiepaws.mobile.domain.repository.AppSettingsRepository
import com.pokiepaws.mobile.domain.repository.AuthRepository
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import com.pokiepaws.mobile.domain.repository.NotificationRepository
import com.pokiepaws.mobile.domain.repository.VetRepository
import com.pokiepaws.mobile.domain.repository.VisitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindAnimalRepository(animalRepositoryImpl: AnimalRepositoryImpl): AnimalRepository

    @Binds
    @Singleton
    fun bindAppSettingsRepository(appSettingsRepositoryImpl: AppSettingsRepositoryImpl): AppSettingsRepository

    @Binds
    @Singleton
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindVisitRepository(visitRepositoryImpl: VisitRepositoryImpl): VisitRepository

    @Binds
    @Singleton
    fun bindClinicRepository(clinicRepositoryImpl: ClinicRepositoryImpl): ClinicRepository

    @Binds
    @Singleton
    fun bindVetRepository(vetRepositoryImpl: VetRepositoryImpl): VetRepository

    @Binds
    @Singleton
    fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
}
