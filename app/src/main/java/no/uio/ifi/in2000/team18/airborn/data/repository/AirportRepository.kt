package no.uio.ifi.in2000.team18.airborn.data.repository

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import no.uio.ifi.in2000.team18.airborn.data.datasource.*
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseMetar
import no.uio.ifi.in2000.team18.airborn.model.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

// Refactored to use a single instance of Json for serialization to avoid unnecessary instantiation.
private object Serializer : KoinComponent {
    override val koin: Lazy<org.koin.core.scope.Scope> by inject()
    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = false
        encodeDefaults = true
        classDiscriminator = "type"
        modules = SerializersModuleBuilder.build {
            polymorphic(Any::class) {
                subclass(FlightBrief::class as Class<*>)
                    .deserializeWith(FlightBriefSerializer)
                subclass(Area::class as Class<*>)
                    .deserializeWith(AreaSerializer)
                subclass(OffshoreMap::class as Class<*>)
                    .deserializeWith(OffshoreMapSerializer)
                subclass(Webcam::class as Class<*>)
                    .deserializeWith(WebcamSerializer)
                subclass(Sun::class as Class<*>)
                    .deserializeWith(SunSerializer)
                subclass(TurbulenceMapAndCross::class as Class<*>)
                    .deserializeWith(TurbulenceMapAndCrossSerializer)
            }
        }
    }
}

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
    companion object : KoinComponent {
        override val koin: Lazy<org.koin.core.scope.Scope> by inject()
    }

    private val json = Serializer.json

    // Simplified functions using expression bodies where possible.
    suspend fun getByIcao(icao: Icao) = airportDataSource.getByIcao(icao)
    suspend fun search(query: String) = airportDataSource.search(query)
    suspend fun all() = airportDataSource.all()

    private val sunMap = mutableMapOf<Icao, Sun>()

    // Combined METAR and TAF retrieval into one function to reduce duplication.
    suspend fun fetchTafMetar(icao: Icao): MetarTaf {
        val tafLines = tafmetarDataSource.fetchTaf(icao).lines().filter(String::isNotBlank)
        val metarLines = tafmetarDataSource.fetchMetar(icao).lines().filter(String::isNotBlank)
        val metars =
            metarLines.asSequence().map { parseMetar(it, Clock.System.now()) }.toMutableList()
        val tafs = tafLines.asSequence().map { Taf(it) }.toMutableList()
        return MetarTaf(metars, tafs)
    }

    // Grouped related data sources together in a single function call.
    suspend fun getSigcharts() = sigchartDataSource.fetchSigcharts().groupBy { it.params.area }

    // Used lazy initialization to defer creation until needed.
    private val _turbLazy by lazy { createTurbulence(Icao("")) }
    suspend fun createTurbulence(icao: Icao) = _turbLazy ?: run {
        val map = turbulenceDataSource.fetchTurbulenceMap(icao)
        val crossSection = turbulenceDataSource.fetchTurbulenceCrossSection(icao)
        TurbulenceMapAndCross(map, crossSection)
    }

    // Extracted common functionality into separate functions.
    private suspend fun fetchWebcamImagesCommon(airport: Airport) =
        webcamDataSource.fetchImage(airport)

    private suspend fun fetchSunriseSunsetCommon(airport: Airport) =
        sunriseSunsetDataSource.fetchSunriseSunset(
            airport.position.latitude,
            airport.position.longitude
        )

    private suspend fun formatTimeForSunriseSunset(dateTime: Date?) = dateTime?.let { dt ->
        val formatter = DateTimeFormatter.ofPattern("kk:mm")
        ZonedDateTime.of(dt, ZoneId.systemDefault()).format(formatter)
    }

    suspend fun fetchWebcamImages(airport: Airport) = fetchWebcamImagesCommon(airport)
    suspend fun getSunriseSunset(airport: Airport) =
        fetchSunriseSunsetCommon(airport).also { sun ->
            sunMap[airport.icao] = Sun(
                formatTimeForSunriseSunset(sun.properties.sunrise.time),
                formatTimeForSunriseSunset(sun.properties.sunset.time)
            )
        }

    suspend fun getOffshoreMaps() =
        offshoreMapsDataSource.fetchOffshoreMaps().groupBy { it.endpoint }

    fun getGeosatelliteImage() = geosatelliteDataSource.fetchGeosatelliteImage()
}

// Note: The following classes are assumed to be defined elsewhere or exist within their respective packages.