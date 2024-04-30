package no.uio.ifi.in2000.team18.airborn.data.repository

import kotlinx.datetime.Clock
import no.uio.ifi.in2000.team18.airborn.data.datasource.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.GeosatelliteDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.OffshoreMapsDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.RadarDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.RouteDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SigchartDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SunriseSunsetDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TafmetarDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TurbulenceDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.WebcamDataSource
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.ParseResult
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseMetar
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.OffshoreMap
import no.uio.ifi.in2000.team18.airborn.model.Radar
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.Turbulence
import no.uio.ifi.in2000.team18.airborn.model.Webcam
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Taf
import no.uio.ifi.in2000.team18.airborn.ui.common.hourMinute
import no.uio.ifi.in2000.team18.airborn.ui.common.toSystemZoneOffset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

// Refactored to use a single instance of Json for serialization to avoid unnecessary instantiation.
@Singleton
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
    private val routeDataSource: RouteDataSource,
) {
    private var sunDataCache = mutableMapOf<Icao, Sun>()
    private var tafMetarDataCache = mutableMapOf<Icao, MetarTaf>()
    private var sigchartDataCache: Map<Area, List<Sigchart>>? = null
    private var turbulenceDataCache = mutableMapOf<Icao, Map<String, List<Turbulence>>?>()
    private var webcamDataCache = mutableMapOf<Airport, List<Webcam>>()
    private var offshoreDataCache: Map<String, List<OffshoreMap>>? = null
    private var geoSatDataCache: String? = null
    private var radarDataCache: List<Radar>? = null
    private var routeDataCache: List<String>? = null

    // Airport logic
    suspend fun getByIcao(icao: Icao) = airportDataSource.getByIcao(icao)

    suspend fun getAirportNearby(airport: Airport, max: Int = 5) =
        airportDataSource.getAirportsNearby(airport, (max * 1.2).roundToInt())
            .map { Airport.fromBuiltinAirport(it) }
            .sortedBy { airport.position.distanceTo(it.position).meters }.take(max)

    suspend fun search(query: String) = airportDataSource.search(query)

    suspend fun all() = airportDataSource.all()

    // TAF/METAR logic
    suspend fun fetchTafMetar(icao: Icao) = tafMetarDataCache[icao] ?: run {
        val tafLines = tafmetarDataSource.fetchTaf(icao).lines().filter(String::isNotBlank)
        val metarLines = tafmetarDataSource.fetchMetar(icao).lines().filter(String::isNotBlank)
        val metars = metarLines.asSequence().map {
            when (val res = parseMetar(it, Clock.System.now())) {
                is ParseResult.Ok -> res.value
                is ParseResult.Error -> Metar.OpaqueMetar(it)
            }
        }.toMutableList()
        val tafs = tafLines.asSequence().map { Taf(it) }.toMutableList()
        MetarTaf(metars, tafs).also { tafMetarDataCache[icao] = it }
    }

    // Sigchart logic
    suspend fun getSigcharts() =
        sigchartDataCache ?: sigchartDataSource.fetchSigcharts().groupBy { it.params.area }
            .also { sigchartDataCache = it }

    // Turbulence logic
    suspend fun fetchTurbulence(icao: Icao) =
        turbulenceDataCache[icao] ?: turbulenceDataSource.fetchTurbulenceMap(icao)
            ?.groupBy { it.params.type }.also { turbulenceDataCache[icao] = it }


    // Webcam Logic
    suspend fun fetchWebcamImages(airport: Airport) = webcamDataCache[airport]
        ?: webcamDataSource.fetchImage(airport).webcams.also { webcamDataCache[airport] = it }


    // Sunrise & Sunset logic
    suspend fun fetchSunriseSunset(airport: Airport) = sunDataCache[airport.icao] ?: run {
        val sun = sunriseSunsetDataSource.fetchSunriseSunset(
            airport.position.latitude, airport.position.longitude
        )
        val newSun =
            if (sun.properties.sunrise.time == null || sun.properties.sunset.time == null) {
                Sun("N/A", "N/A")
            } else {
                val sunrise = sun.properties.sunrise.time.toSystemZoneOffset().hourMinute()
                val sunset = sun.properties.sunset.time.toSystemZoneOffset().hourMinute()
                Sun(sunrise, sunset)
            }
        newSun.also { sunDataCache[airport.icao] = it }
    }

    suspend fun getOffshoreMaps() =
        offshoreDataCache ?: offshoreMapsDataSource.fetchOffshoreMaps().groupBy { it.endpoint }
            .also { offshoreDataCache = it }

    fun getGeosatelliteImage() = geoSatDataCache ?: geosatelliteDataSource.fetchGeosatelliteImage()
        .also { geoSatDataCache = it }

    // Uri's with animation parameter dont work when time is present in the uri string
    // Dont know why, ask MET
    suspend fun fetchRadarAnimations() =
        radarDataCache ?: radarDataSource.fetchRadarAnimations().map { radar ->
            radar.copy(uri = removeTimeFromUrl(radar.uri))
        }.also { radarDataCache = it }

    private fun removeTimeFromUrl(uri: String): String {
        val startIndex = uri.indexOf("time=")
        val endIdex = uri.indexOf("&", startIndex)

        return uri.replaceRange(startIndex, endIdex + 1, "")
    }

    suspend fun isRoute(route: String): Boolean =
        route in (routeDataCache ?: routeDataSource.fetchAllAvailableRoutes().also {
            routeDataCache = it
        })

    suspend fun fetchRoute(departureIcao: Icao, arrivalIcao: Icao) =
        routeDataSource.fetchRoute("iga-${departureIcao.code}-${arrivalIcao.code}") // only iga is relevant for our case

    fun clearCache() {
        sunDataCache.clear()
        tafMetarDataCache.clear()
        sigchartDataCache = null
        turbulenceDataCache.clear()
        webcamDataCache.clear()
        offshoreDataCache = null
        geoSatDataCache = null
        radarDataCache = null
        routeDataCache = null
    }
}