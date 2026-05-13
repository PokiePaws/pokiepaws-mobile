package com.pokiepaws.mobile.di

import com.pokiepaws.mobile.data.repository.AnimalRepositoryImpl
import com.pokiepaws.mobile.data.repository.AuthRepositoryImpl
import com.pokiepaws.mobile.data.repository.ClinicRepositoryImpl
import com.pokiepaws.mobile.data.repository.NotificationRepositoryImpl
import com.pokiepaws.mobile.data.repository.VetRepositoryImpl
import com.pokiepaws.mobile.data.repository.VisitRepositoryImpl
import com.pokiepaws.mobile.domain.repository.AnimalRepository
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
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAnimalRepository(animalRepositoryImpl: AnimalRepositoryImpl): AnimalRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVisitRepository(visitRepositoryImpl: VisitRepositoryImpl): VisitRepository

    @Binds
    @Singleton
    abstract fun bindClinicRepository(clinicRepositoryImpl: ClinicRepositoryImpl): ClinicRepository

    @Binds
    @Singleton
    abstract fun bindVetRepository(vetRepositoryImpl: VetRepositoryImpl): VetRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
}
