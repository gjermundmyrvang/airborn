package no.uio.ifi.in2000.team18.airborn.data.repository

import no.uio.ifi.in2000.team18.airborn.data.datasource.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SigchartDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.SunriseSunsetDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TafmetarDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.TurbulenceDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.WebcamDataSource
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.Webcam
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Taf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.TurbulenceMapAndCross
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AirportRepository @Inject constructor(
    private val airportDataSource: AirportDataSource,
    private val tafmetarDataSource: TafmetarDataSource,
    private val sigchartDataSource: SigchartDataSource,
    private val turbulenceDataSource: TurbulenceDataSource,
    private val webcamDataSource: WebcamDataSource,
    private val sunriseSunsetDataSource: SunriseSunsetDataSource
) {
    // Airport logic
    suspend fun getByIcao(icao: Icao): Airport? = airportDataSource.getByIcao(icao)
    suspend fun search(query: String): List<Airport> = airportDataSource.search(query)
    suspend fun all(): List<Airport> = airportDataSource.all()

    private var sunMap = HashMap<Icao, Sun>()


    // TAF/METAR logic
    suspend fun fetchTafMetar(icao: Icao): MetarTaf {
        val tafList: List<Taf> =
            tafmetarDataSource.fetchTaf(icao).lines().filter { it.isNotEmpty() }.map { Taf(it) }
        val metarList: List<Metar> =
            tafmetarDataSource.fetchMetar(icao).lines().filter { it.isNotEmpty() }.map { Metar(it) }
        return MetarTaf(metars = metarList, tafs = tafList)
    }

    // Sigchart logic
    suspend fun getSigcharts(): Map<Area, List<Sigchart>> {
        val sigcharts = sigchartDataSource.fetchSigcharts()
        val sigMap = sigcharts.groupBy { it.params.area }
        return sigMap
    }

    // Turbulence logic
    suspend fun createTurbulence(icao: Icao): TurbulenceMapAndCross? {
        val map = turbulenceDataSource.fetchTurbulenceMap(icao)
        val crossSection = turbulenceDataSource.fetchTurbulenceCrossSection(icao)
        if (map == null || crossSection == null) return null
        return TurbulenceMapAndCross(
            map,
            crossSection,
        )
    }

    suspend fun fetchWebcamImages(airport: Airport): List<Webcam> =
        webcamDataSource.fetchImage(airport)


    // Sunrise & Sunset logic
    suspend fun getSunriseSunset(airport: Airport): Sun {

        val sunData = sunMap[airport.icao]

        return if (sunData != null) {
            sunData
        } else {

            val sun = sunriseSunsetDataSource.fetchSunriseSunset(
                airport.position.latitude, airport.position.longitude
            )

            val formatter = DateTimeFormatter.ofPattern("kk:mm")

            val sunrise = ZonedDateTime.parse(sun.properties.sunrise.time)
                .withZoneSameInstant(ZoneId.systemDefault()).format(formatter)

            val sunset = ZonedDateTime.parse(sun.properties.sunset.time)
                .withZoneSameInstant(ZoneId.systemDefault()).format(formatter)

            val newSun = Sun(sunrise, sunset)

            sunMap[airport.icao] = newSun
            newSun
        }
    }
}