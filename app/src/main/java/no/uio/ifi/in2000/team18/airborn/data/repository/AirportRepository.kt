package no.uio.ifi.in2000.team18.airborn.data.repository

import kotlinx.datetime.Clock
import no.uio.ifi.in2000.team18.airborn.data.datasource.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.GeosatelliteDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.OffshoreMapsDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SigchartDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SunriseSunsetDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TafmetarDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TurbulenceDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.WebcamDataSource
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseMetar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Taf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.TurbulenceMapAndCross
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// Refactored to use a single instance of Json for serialization to avoid unnecessary instantiation.

class AirportRepository @Inject constructor(
    private val airportDataSource: AirportDataSource,
    private val tafmetarDataSource: TafmetarDataSource,
    private val sigchartDataSource: SigchartDataSource,
    private val turbulenceDataSource: TurbulenceDataSource,
    private val webcamDataSource: WebcamDataSource,
    private val sunriseSunsetDataSource: SunriseSunsetDataSource,
    private val offshoreMapsDataSource: OffshoreMapsDataSource,
    private val geosatelliteDataSource: GeosatelliteDataSource,
) {
    // Airport logic
    suspend fun getByIcao(icao: Icao) = airportDataSource.getByIcao(icao)
    suspend fun search(query: String) = airportDataSource.search(query)
    suspend fun all() = airportDataSource.all()

    private val sunDataCache =
        mutableMapOf<Icao, no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun>()

    // TAF/METAR logic
    suspend fun fetchTafMetar(icao: Icao): MetarTaf {
        val tafLines = tafmetarDataSource.fetchTaf(icao).lines().filter(String::isNotBlank)
        val metarLines = tafmetarDataSource.fetchMetar(icao).lines().filter(String::isNotBlank)
        val metars =
            metarLines.asSequence().map { parseMetar(it, Clock.System.now()) }.toMutableList()
        val tafs = tafLines.asSequence().map { Taf(it) }.toMutableList()
        return MetarTaf(metars.map { it.expect() }, tafs)
    }

    // Sigchart logic
    suspend fun getSigcharts() = sigchartDataSource.fetchSigcharts().groupBy { it.params.area }

    // Turbulence logic
    suspend fun fetchTurbulence(icao: Icao): TurbulenceMapAndCross? {
        val map = turbulenceDataSource.fetchTurbulenceMap(icao)
        val crossSection = turbulenceDataSource.fetchTurbulenceCrossSection(icao)
        if (map == null || crossSection == null) return null
        return TurbulenceMapAndCross(map, crossSection)
    }

    // Webcam Logic
    suspend fun fetchWebcamImages(airport: Airport) = webcamDataSource.fetchImage(airport).webcams

    // Sunrise & Sunset logic
    suspend fun fetchSunriseSunset(airport: Airport): Sun {
        sunDataCache[airport.icao]?.also { return it }
        val sun = sunriseSunsetDataSource.fetchSunriseSunset(
            airport.position.latitude, airport.position.longitude
        )
        if (sun.properties.sunrise.time == null) {
            return Sun("N/A", "N/A")
        }
        val formatter = DateTimeFormatter.ofPattern("kk:mm")
        val sunrise = ZonedDateTime.parse(sun.properties.sunrise.time)
            .withZoneSameInstant(ZoneId.systemDefault()).format(formatter)
        val sunset = ZonedDateTime.parse(sun.properties.sunset.time)
            .withZoneSameInstant(ZoneId.systemDefault()).format(formatter)
        val newSun = Sun(sunrise, sunset)
        sunDataCache[airport.icao] = newSun
        return newSun
    }

    suspend fun getOffshoreMaps() =
        offshoreMapsDataSource.fetchOffshoreMaps().groupBy { it.endpoint }

    fun getGeosatelliteImage() = geosatelliteDataSource.fetchGeosatelliteImage()
}