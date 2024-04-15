package no.uio.ifi.in2000.team18.airborn.data.repository

import no.uio.ifi.in2000.team18.airborn.data.datasource.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import javax.inject.Inject

class AirportRepository @Inject constructor(private val airportDataSource: AirportDataSource) {
    suspend fun getByIcao(icao: Icao): Airport? = airportDataSource.getByIcao(icao)
    suspend fun search(query: String): List<Airport> = airportDataSource.search(query)
    suspend fun all(): List<Airport> = airportDataSource.all()
}