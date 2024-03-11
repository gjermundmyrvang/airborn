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

class TurbulenceDataSource {
    val client = HttpClient(CIO) {
        defaultRequest {
            header("X-Gravitee-API-Key", "95d6f7c0-9b84-4002-89ba-483ac0f827c6")
            url("https://gw-uio.intark.uh-it.no/in2000/")
        }
        install(ContentNegotiation) {
            gson()
        }
    }

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
