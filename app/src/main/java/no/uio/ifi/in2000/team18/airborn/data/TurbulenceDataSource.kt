package no.uio.ifi.in2000.team18.airborn.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.Turbulence
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import javax.inject.Inject

/**
Turbulence data is generated every 3 hours, for 0 - 18 hours ahead
 */
class TurbulenceDataSource @Inject constructor(
    val client: HttpClient
) {

    suspend fun fetchTurbulenceCrossSection(icao: Icao): List<Turbulence>? {
        val url = "weatherapi/turbulence/2.0/available.json?type=cross_section&icao=$icao"
        val res = client.get(url)
        return if (res.status.value >= 400) null else res.body()
    }

    suspend fun fetchTurbulenceMap(icao: Icao): List<Turbulence>? {
        val url = "weatherapi/turbulence/2.0/available.json?type=map&icao=$icao"
        val res = client.get(url)
        return if (res.status.value >= 400) null else res.body()
    }
}

