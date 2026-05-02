package com.pokiepaws.mobile.di

import com.pokiepaws.mobile.data.repository.AnimalRepositoryImpl
import com.pokiepaws.mobile.domain.repository.AnimalRepository
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
    abstract fun bindAnimalRepository(
        animalRepositoryImpl: AnimalRepositoryImpl
    ): AnimalRepository
}
