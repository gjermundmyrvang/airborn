package no.uio.ifi.in2000.team18.airborn.data.repository

import no.uio.ifi.in2000.team18.airborn.data.datasource.TafmetarDataSource
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Taf
import javax.inject.Inject

class TafmetarRepository @Inject constructor(private val tafmetarDataSource: TafmetarDataSource) {
    suspend fun fetchTafMetar(icao: Icao): MetarTaf {
        val tafList: List<Taf> =
            tafmetarDataSource.fetchTaf(icao).lines().filter { it.isNotEmpty() }.map { Taf(it) }
        val metarList: List<Metar> =
            tafmetarDataSource.fetchMetar(icao).lines().filter { it.isNotEmpty() }.map { Metar(it) }
        return MetarTaf(metars = metarList, tafs = tafList)
    }
}