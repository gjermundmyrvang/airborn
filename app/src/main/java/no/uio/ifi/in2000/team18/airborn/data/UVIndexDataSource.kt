package no.uio.ifi.in2000.team18.airborn.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.team18.airborn.model.UVResponse
import no.uio.ifi.in2000.team18.airborn.model.UVResult
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport

class UVIndexDataSource {
    private val client: HttpClient = HttpClient(CIO) {
        defaultRequest {
            header("x-access-token", "openuv-5ara6jrluh3gzjv-io")
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchUVData(airport: Airport): UVResult {
        val response: UVResponse =
            client.get("https://api.openuv.io/api/v1/uv?lat=${airport.position.latitude}&lng=${airport.position.longitude}&alt=100&dt=")
                .body()
        return response.result
    }

}