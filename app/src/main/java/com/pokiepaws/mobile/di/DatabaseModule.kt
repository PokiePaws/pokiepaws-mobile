package com.pokiepaws.mobile.di

import android.content.Context
import androidx.room.Room
import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.room.PokieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): PokieDatabase {
        return Room.databaseBuilder(
            context,
            PokieDatabase::class.java,
            "pokie_paws_db",
        ).build()
    }

    @Provides
    fun provideNotificationDao(db: PokieDatabase): NotificationDao {
        return db.notificationDao()
    }
}
