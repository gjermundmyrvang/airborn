package no.uio.ifi.in2000.team18.airborn.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team18.airborn.data.datasource.AppDatabase
import no.uio.ifi.in2000.team18.airborn.data.dao.BuiltinAirportDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "airborn.db"
        ).createFromAsset("database/airborn.db").build()

    @Provides
    @Singleton
    fun provideBuiltinAirportDao(database: AppDatabase): BuiltinAirportDao =
        database.builtinAirportDao
}