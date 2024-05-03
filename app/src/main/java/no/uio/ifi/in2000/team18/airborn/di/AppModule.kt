package no.uio.ifi.in2000.team18.airborn.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.team18.airborn.data.dao.BuiltinAirportDao
import no.uio.ifi.in2000.team18.airborn.data.datasource.AppDatabase
import no.uio.ifi.in2000.team18.airborn.ui.connectivity.ConnectivityObserver
import no.uio.ifi.in2000.team18.airborn.ui.connectivity.NetworkConnectivityObserver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient = HttpClient(CIO) {
        defaultRequest {
            header("X-Gravitee-API-Key", "95d6f7c0-9b84-4002-89ba-483ac0f827c6")
            url("https://gw-uio.intark.uh-it.no/in2000/")
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "airborn.db"
        ).createFromAsset("database/airborn.db")
            .addMigrations(object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE builtin_airport ADD COLUMN is_favourite INTEGER NOT NULL DEFAULT false;")
                }
            })
            .build()

    @Provides
    @Singleton
    fun provideBuiltinAirportDao(database: AppDatabase): BuiltinAirportDao =
        database.builtinAirportDao

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver =
        NetworkConnectivityObserver(context)
}