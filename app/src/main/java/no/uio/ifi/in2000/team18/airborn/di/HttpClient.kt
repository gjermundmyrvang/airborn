package no.uio.ifi.in2000.team18.airborn.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HttpClient {
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
}
