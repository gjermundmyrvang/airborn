package no.uio.ifi.in2000.team18.airborn.data.repository

import no.uio.ifi.in2000.team18.airborn.data.datasource.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SigchartDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TafmetarDataSource
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Taf
import javax.inject.Inject

class AirportRepository @Inject constructor(
    private val airportDataSource: AirportDataSource,
    private val tafmetarDataSource: TafmetarDataSource,
    private val sigchartDataSource: SigchartDataSource,
) {
    suspend fun getByIcao(icao: Icao): Airport? = airportDataSource.getByIcao(icao)
    suspend fun search(query: String): List<Airport> = airportDataSource.search(query)
    suspend fun all(): List<Airport> = airportDataSource.all()
    suspend fun fetchTafMetar(icao: Icao): MetarTaf {
        val tafList: List<Taf> =
            tafmetarDataSource.fetchTaf(icao).lines().filter { it.isNotEmpty() }.map { Taf(it) }
        val metarList: List<Metar> =
            tafmetarDataSource.fetchMetar(icao).lines().filter { it.isNotEmpty() }.map { Metar(it) }
        return MetarTaf(metars = metarList, tafs = tafList)
    }

    suspend fun getSigcharts(): Map<Area, List<Sigchart>> {
        val sigcharts = sigchartDataSource.fetchSigcharts()
        val sigMap = sigcharts.groupBy { it.params.area }
        return sigMap
    }
}