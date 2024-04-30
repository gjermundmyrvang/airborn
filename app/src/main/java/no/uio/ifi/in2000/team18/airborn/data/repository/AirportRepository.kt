package no.uio.ifi.in2000.team18.airborn.data.repository

import kotlinx.datetime.Clock
import no.uio.ifi.in2000.team18.airborn.data.datasource.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.GeosatelliteDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.OffshoreMapsDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.RadarDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SigchartDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SunriseSunsetDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TafmetarDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TurbulenceDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.WebcamDataSource
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.ParseResult
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseMetar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Taf
import no.uio.ifi.in2000.team18.airborn.ui.common.hourMinute
import no.uio.ifi.in2000.team18.airborn.ui.common.toSystemZoneOffset
import javax.inject.Inject
import kotlin.math.roundToInt

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
    private val radarDataSource: RadarDataSource,
) {
    // Airport logic
    suspend fun getByIcao(icao: Icao) = airportDataSource.getByIcao(icao)
    suspend fun getAirportNearby(airport: Airport, max: Int = 5) =
        airportDataSource.getAirportsNearby(airport, (max * 1.2).roundToInt())
            .map { Airport.fromBuiltinAirport(it) }
            .sortedBy { airport.position.distanceTo(it.position).meters }.take(max)

    suspend fun search(query: String) = airportDataSource.search(query)
    suspend fun all() = airportDataSource.all()

    private val sunDataCache = mutableMapOf<Icao, Sun>()

    // TAF/METAR logic
    suspend fun fetchTafMetar(icao: Icao): MetarTaf {
        val tafLines = tafmetarDataSource.fetchTaf(icao).lines().filter(String::isNotBlank)
        val metarLines = tafmetarDataSource.fetchMetar(icao).lines().filter(String::isNotBlank)
        val metars = metarLines.asSequence().map {
            when (val res = parseMetar(it, Clock.System.now())) {
                is ParseResult.Ok -> res.value
                is ParseResult.Error -> Metar.OpaqueMetar(it)
            }
        }.toMutableList()
        val tafs = tafLines.asSequence().map { Taf(it) }.toMutableList()
        return MetarTaf(metars, tafs)
    }

    // Sigchart logic
    suspend fun getSigcharts() = sigchartDataSource.fetchSigcharts().groupBy { it.params.area }

    // Turbulence logic
    suspend fun fetchTurbulence(icao: Icao) =
        turbulenceDataSource.fetchTurbulenceMap(icao)
            ?.groupBy { it.params.type }

    // Webcam Logic
    suspend fun fetchWebcamImages(airport: Airport) = webcamDataSource.fetchImage(airport).webcams

    // Sunrise & Sunset logic
    suspend fun fetchSunriseSunset(airport: Airport): Sun {
        sunDataCache[airport.icao]?.also { return it }
        val sun = sunriseSunsetDataSource.fetchSunriseSunset(
            airport.position.latitude, airport.position.longitude
        )
        val newSun =
            if (sun.properties.sunrise.time == null || sun.properties.sunset.time == null) {
                Sun("N/A", "N/A")
            } else {
                val sunrise = sun.properties.sunrise.time
                    .toSystemZoneOffset().hourMinute()
                val sunset = sun.properties.sunset.time
                    .toSystemZoneOffset().hourMinute()
                Sun(sunrise, sunset)
            }
        sunDataCache[airport.icao] = newSun
        return newSun
    }

    suspend fun getOffshoreMaps() =
        offshoreMapsDataSource.fetchOffshoreMaps().groupBy { it.endpoint }

    fun getGeosatelliteImage() = geosatelliteDataSource.fetchGeosatelliteImage()

    // Uri's dont work when time is present in the uri string
    // Dont know why, ask MET
    suspend fun fetchRadarAnimations() = radarDataSource.fetchRadarAnimations().map { radar ->
        radar.copy(uri = removeTimeFromUrl(radar.uri))
    }

    private fun removeTimeFromUrl(uri: String): String {
        val startIndex = uri.indexOf("time=")
        val endIdex = uri.indexOf("&", startIndex)

        return uri.replaceRange(startIndex, endIdex + 1, "")
    }
}