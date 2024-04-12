package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Taf
import javax.inject.Inject


class TafmetarDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchTaf(icao: Icao): String {
        return client.get("weatherapi/tafmetar/1.0/taf.txt?icao=$icao").body()
    }

    suspend fun fetchMetar(icao: Icao): String {
        return client.get("weatherapi/tafmetar/1.0/metar.txt?icao=$icao").body()
    }

    suspend fun fetchTafMetar(icao: Icao): MetarTaf {
        val tafList: List<Taf> = fetchTaf(icao).lines().filter { it.isNotEmpty() }.map { Taf(it) }
        val metarList: List<Metar> =
            fetchMetar(icao).lines().filter { it.isNotEmpty() }.map { Metar(it) }
        return MetarTaf(metars = metarList, tafs = tafList)
    }
}


