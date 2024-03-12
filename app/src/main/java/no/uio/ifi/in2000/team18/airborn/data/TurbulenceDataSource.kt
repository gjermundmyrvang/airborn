package no.uio.ifi.in2000.team18.airborn.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.team18.airborn.model.Turbulence
import javax.inject.Inject

class TurbulenceDataSource @Inject constructor(
    val client: HttpClient
) {

    suspend fun fetchTurbulenceCross_section(): List<Turbulence> {
        val res =
            client.get("weatherapi/turbulence/2.0/available.json?type=cross_section&icao=ENBN")
        return res.body()
    }

    suspend fun fetchTurbulenceMap(): List<Turbulence> {
        val res = client.get("weatherapi/turbulence/2.0/available.json?type=map&icao=ENBN")
        return res.body()
    }
}
