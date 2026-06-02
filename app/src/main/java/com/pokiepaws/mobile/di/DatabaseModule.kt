package com.pokiepaws.mobile.di

import android.content.Context
import androidx.room.Room
import com.pokiepaws.mobile.data.local.DatabasePassphraseProvider
import com.pokiepaws.mobile.data.local.dao.AnimalDao
import com.pokiepaws.mobile.data.local.dao.ClinicDao
import com.pokiepaws.mobile.data.local.dao.NotificationDao
import com.pokiepaws.mobile.data.local.dao.OwnerProfileDao
import com.pokiepaws.mobile.data.local.dao.PrescriptionDao
import com.pokiepaws.mobile.data.local.dao.VetDao
import com.pokiepaws.mobile.data.local.dao.VisitDao
import com.pokiepaws.mobile.data.local.room.PokieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val LEGACY_DB_NAME = "pokie_paws_db"
    private const val ENCRYPTED_DB_NAME = "pokie_paws_db_enc"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        passphraseProvider: DatabasePassphraseProvider,
    ): PokieDatabase {
        context.deleteDatabase(LEGACY_DB_NAME)
        context.deleteDatabase("$LEGACY_DB_NAME-wal")
        context.deleteDatabase("$LEGACY_DB_NAME-shm")

        val factory = SupportFactory(passphraseProvider.getPassphrase())

        return Room.databaseBuilder(
            context,
            PokieDatabase::class.java,
            ENCRYPTED_DB_NAME,
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideNotificationDao(db: PokieDatabase): NotificationDao = db.notificationDao()

    @Provides fun provideAnimalDao(db: PokieDatabase): AnimalDao = db.animalDao()

    @Provides fun provideClinicDao(db: PokieDatabase): ClinicDao = db.clinicDao()

    @Provides fun provideVisitDao(db: PokieDatabase): VisitDao = db.visitDao()

    @Provides fun provideVetDao(db: PokieDatabase): VetDao = db.vetDao()

    @Provides fun provideOwnerProfileDao(db: PokieDatabase): OwnerProfileDao = db.ownerProfileDao()

    @Provides fun providePrescriptionDao(db: PokieDatabase): PrescriptionDao = db.prescriptionDao()
}
