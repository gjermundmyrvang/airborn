package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import javax.inject.Inject


class TafmetarDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchTaf(icao: Icao): String {
        val res = client.get("weatherapi/tafmetar/1.0/taf.txt?icao=$icao")
        if (res.status.value >= 400)
            throw ApiException()
        return res.body()
    }

    suspend fun fetchMetar(icao: Icao): String {
        val res = client.get("weatherapi/tafmetar/1.0/metar.txt?icao=$icao")
        if (res.status.value >= 400)
            throw ApiException()
        return res.body()
    }
}


