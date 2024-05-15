package no.uio.ifi.in2000.team18.airborn.data.repository

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.data.datasource.GribDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.LocationForecastDataSource
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.GribFile
import no.uio.ifi.in2000.team18.airborn.model.NextHourDetails
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.Pressure
import no.uio.ifi.in2000.team18.airborn.model.RouteIsobaric
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.Temperature
import no.uio.ifi.in2000.team18.airborn.model.TimeSeries
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricLayer
import no.uio.ifi.in2000.team18.airborn.model.mps
import no.uio.ifi.in2000.team18.airborn.ui.common.systemDayOfWeek
import no.uio.ifi.in2000.team18.airborn.ui.common.systemHourMinute
import no.uio.ifi.in2000.team18.airborn.ui.common.toSystemZoneOffset
import ucar.nc2.dt.GridDatatype
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

const val TEMPERATURE_LAPSE_RATE: Double = 0.0065 // in (K/m)
const val HEIGHT_CALCULATION_EXPONENT: Double = 1 / 5.25579
const val HEIGHT_CALCULATION_FACTOR: Double = 29.2717 // R/(g*M) = 8.314.. / (9.806.. * 0.02896..)


@Singleton
class WeatherRepository @Inject constructor(
    private val locationForecastDataSource: LocationForecastDataSource,
    private val gribDataSource: GribDataSource,
) {
    private var weatherDataCache = ConcurrentHashMap(mutableMapOf<Airport, List<WeatherDay>>())

    private var isobaricDataCache =
        ConcurrentHashMap(mutableMapOf<Pair<Position, ZonedDateTime?>, Map<Int, List<Double>>>())
    private var allGribFiles = ConcurrentHashMap(mapOf<ZonedDateTime, GribFile>())
    private var timeSeriesMutex = Mutex()
    private var timeSeriesDataCache: List<ZonedDateTime> = listOf()

    private suspend fun getIsobaricData(
        position: Position, time: ZonedDateTime? = null
    ): IsobaricData {
        // fallback function, only if everything else fails to get a grib file
        suspend fun grabLastGrib(): GribFile =
            gribDataSource.availableGribFiles().last()

        val gribFile: GribFile =
            if (timeSeriesDataCache.isEmpty()) { //Runs on init
                val gribFiles = gribDataSource.availableGribFiles()
                allGribFiles = ConcurrentHashMap(gribFiles.associateBy { it.params.time })
                timeSeriesMutex.withLock { this.timeSeriesDataCache = allGribFiles.keys.sorted() }

                val firstTime = timeSeriesMutex.withLock {
                    this.timeSeriesDataCache.first()
                }
                allGribFiles[firstTime] ?: gribFiles.last()
            } else {
                if (time != null) allGribFiles[time] ?: grabLastGrib()
                else {
                    val firstTime = timeSeriesMutex.withLock {
                        this.timeSeriesDataCache.first()
                    }
                    allGribFiles[firstTime] ?: grabLastGrib()
                }
            }


        val windsAloft = isobaricDataCache[Pair(position, time)] ?: gribDataSource.useGribFile(
            gribFile
        ) { dataset ->
            val windU = dataset.grids.find { it.shortName == "u-component_of_wind_isobaric" }!!
            val windV = dataset.grids.find { it.shortName == "v-component_of_wind_isobaric" }!!
            val temperature = dataset.grids.find { it.shortName == "Temperature_isobaric" }!!

            Log.d("grib", "${windU.fullName} ${windV.fullName} ${temperature.fullName}")

            temperature.coordinateSystem.verticalAxis.names.mapIndexed { i, named ->
                (named.name.trim().toInt() / 100) to listOf(
                    temperature.sampleAtPosition(position, i).toDouble(),
                    windU.sampleAtPosition(position, i).toDouble(),
                    windV.sampleAtPosition(position, i).toDouble(),
                )
            }.toMap().filterKeys { it > 500.0 }// only use high pressureLayers = low altitude.also
                .also { isobaricDataCache[Pair(position, time)] = it }
        }

        val airPressureASL = getAirPressureAtSeaLevel(position, time)
        val layers = windsAloft.map { (key, value) ->
            val layer = IsobaricLayer(
                pressure = Pressure(key.toDouble()),
                temperature = Temperature(value[0] - 273.15),
                uWind = value[1],
                vWind = value[2]
            )
            layer.windFromDirection = Direction.fromWindUV(layer.uWind, layer.vWind)
            layer.windSpeed = calculateWindSpeed(layer.uWind, layer.vWind)
            layer.height =
                calculateHeight(key.toDouble(), layer.temperature.kelvin, airPressureASL.hpa)
            layer
        }.filter {
            val h = it.height
            val maxHeight = 12000
            val result = if (h != null) (h.feet <= maxHeight) else false
            result
        }
        return IsobaricData(
            position,
            gribFile.params.time.toSystemZoneOffset(),
            layers,
            timeSeriesDataCache
        )

    }

    suspend fun getRouteIsobaric(
        departure: Airport, arrival: Airport, pos: Position, time: ZonedDateTime? = null
    ): RouteIsobaric {
        val distance = departure.position.distanceTo(arrival.position)
        val bearing = departure.position.bearingTo(arrival.position)
        return RouteIsobaric(
            departure = departure,
            arrival = arrival,
            isobaric = getIsobaricData(pos, time),
            distance = distance,
            bearing = bearing,
            currentPos = pos,
        )
    }

    private fun calculateWindSpeed(uWind: Double, vWind: Double): Speed =
        (sqrt(uWind.pow(2) + vWind.pow(2))).mps

    /**
     * Calculate height of isobaric layer, using the formula with non-null lapse rate
     * https://en.wikipedia.org/wiki/Barometric_formula
     * https://physics.stackexchange.com/questions/333475/how-to-calculate-altitude-from-current-temperature-and-pressure
     *
     *
     * @param refTemperature default value according to table on wikipedia (see above)
     * @param pressureLevelZero the actual pressure at sea level
     */
    private fun calculateHeight(
        pressure: Double,
        refTemperature: Double = 288.15,
        pressureLevelZero: Double = 1013.25,
        useLapseRate: Boolean = false,
    ): Distance {
        val result = if (useLapseRate) { // might be useful if live data not available
            refTemperature * (1 - (pressure / pressureLevelZero).pow(
                HEIGHT_CALCULATION_EXPONENT
            )) / TEMPERATURE_LAPSE_RATE
        } else -refTemperature * ln(pressure / pressureLevelZero) * HEIGHT_CALCULATION_FACTOR
        Log.d(
            "height",
            "calculate height with pressure $pressure, refTemp $refTemperature, result $result meters"
        )
        return Distance(result)
    }

    private suspend fun getAirPressureAtSeaLevel(
        position: Position,
        time: ZonedDateTime?
    ): Pressure {
        val availableTimeSeries =
            locationForecastDataSource.fetchForecast(position, "compact").properties.timeseries
        val availableTimes = availableTimeSeries.mapIndexed { index, timeSeries ->
            Pair(index, timeSeries.time)
        }
        val preferredTime =
            time ?: ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS)
                .withZoneSameInstant(ZoneId.of("UTC"))
        Log.d("grib", "preferredTime: $preferredTime")
        val index =
            availableTimes.firstOrNull {
                it.second == preferredTime
            }?.first ?: 0
        return availableTimeSeries[index].data.instant.details.airPressureAtSeaLevel
    }

    private val english = mapOf(
        "clearsky" to "Clear sky",
        "fair" to "Fair",
        "partlycloudy" to "Partly cloudy",
        "cloudy" to "Cloudy",
        "lightrainshowers" to "Light rain showers",
        "rainshowers" to "Rain showers",
        "heavyrainshowers" to "Heavy rain showers",
        "lightrainshowersandthunder" to "Light rain showers and thunder",
        "rainshowersandthunder" to "Rain showers and thunder",
        "heavyrainshowersandthunder" to "Heavy rain showers and thunder",
        "lightsleetshowers" to "Light sleet showers",
        "sleetshowers" to "Sleet showers",
        "heavysleetshowers" to "Heavy sleet showers",
        "lightssleetshowersandthunder " to "Light sleet showers and thunder",
        "sleetshowersandthunder" to "Sleet showers and thunder",
        "heavysleetshowersandthunder" to "Heavy sleet showers and thunder",
        "lightsnowshowers" to "Light snow showers",
        "snowshowers" to "Snow showers",
        "heavysnowshowers" to "Heavy snow showers",
        "lightssnowshowersandthunder" to "Light snow showers and thunder",
        "snowshowersandthunder" to "Snow showers and thunder",
        "heavysnowshowersandthunder" to "Heavy snow showers and thunder",
        "lightrain" to "Light rain",
        "rain" to "Rain",
        "heavyrain" to "Heavy rain",
        "lightrainandthunder" to "Light rain and thunder",
        "rainandthunder" to "Rain and thunder",
        "heavyrainandthunder" to "Heavy rain and thunder",
        "lightsleet" to "Light sleet",
        "sleet" to "Sleet",
        "heavysleet" to "Heavy sleet",
        "lightsleetandthunder" to "Light sleet and thunder",
        "sleetandthunder" to "Sleet and thunder",
        "heavysleetandthunder" to "Heavy sleet and thunder",
        "lightsnow" to "Light snow",
        "snow" to "Snow",
        "heavysnow" to "Heavy snow",
        "lightsnowandthunder" to "Light snow and thunder",
        "snowandthunder" to "Snow and thunder",
        "heavysnowandthunder" to "Heavy snow and thunder",
        "fog" to "Fog",
    )

    suspend fun getWeatherDays(airport: Airport) = weatherDataCache[airport] ?: mapToWeatherDay(
        locationForecastDataSource.fetchForecast(airport.position).properties.timeseries
    ).also { weatherDataCache[airport] = it }


    private fun mapToWeatherDay(timeseries: List<TimeSeries>): List<WeatherDay> {
        val groupedByDate = timeseries.groupBy { it.time.dayOfMonth }
        return groupedByDate.map { (_, timeSeriesList) ->
            WeatherDay(date = timeSeriesList.first().time.systemDayOfWeek(),
                weather = timeSeriesList.map { timeSeries ->
                    WeatherHour(
                        time = timeSeries.time.systemHourMinute(),
                        weatherDetails = timeSeries.data.instant.details,
                        nextOneHour = if (timeSeries.data.next_1_hours != null) english[timeSeries.data.next_1_hours.summary.symbol_code.substringBefore(
                            "_"
                        )]?.let {
                            NextHourDetails(
                                symbol_code = it,
                                icon = iconMapper(timeSeries.data.next_1_hours.summary.symbol_code),
                                precipitation_amount = timeSeries.data.next_1_hours.details["precipitation_amount"]
                            )
                        } else null,
                        nextSixHour = if (timeSeries.data.next_6_hours != null) english[timeSeries.data.next_6_hours.summary.symbol_code.substringBefore(
                            "_"
                        )]?.let {
                            NextHourDetails(
                                symbol_code = it,
                                icon = iconMapper(timeSeries.data.next_6_hours.summary.symbol_code),
                                precipitation_amount = timeSeries.data.next_6_hours.details["precipitation_amount"]
                            )
                        } else null,
                        nextTwelweHour = if (timeSeries.data.next_12_hours != null) english[timeSeries.data.next_12_hours.summary.symbol_code.substringBefore(
                            "_"
                        )]?.let {
                            NextHourDetails(
                                symbol_code = it,
                                icon = iconMapper(timeSeries.data.next_12_hours.summary.symbol_code),
                                precipitation_amount = timeSeries.data.next_12_hours.details["precipitation_amount"]
                            )
                        } else null,
                    )
                })
        }
    }

    private fun iconMapper(iconAsString: String): Int {
        return when (iconAsString) {
            "clearsky_day" -> R.drawable.clearsky_day
            "clearsky_night" -> R.drawable.clearsky_night
            "clearsky_polartwilight" -> R.drawable.clearsky_polartwilight
            "cloudy" -> R.drawable.cloudy
            "fair_day" -> R.drawable.fair_day
            "fair_night" -> R.drawable.fair_night
            "fair_polartwilight" -> R.drawable.fair_polartwilight
            "fog" -> R.drawable.fog
            "heavyrain" -> R.drawable.heavyrain
            "heavyrainandthunder" -> R.drawable.heavyrainandthunder
            "heavyrainshowers_day" -> R.drawable.heavyrainshowers_day
            "heavyrainshowers_night" -> R.drawable.heavyrainshowers_night
            "heavyrainshowers_polartwilight" -> R.drawable.heavyrainshowers_polartwilight
            "heavyrainshowersandthunder_day" -> R.drawable.heavyrainshowersandthunder_day
            "heavyrainshowersandthunder_night" -> R.drawable.heavyrainshowersandthunder_night
            "heavyrainshowersandthunder_polartwilight" -> R.drawable.heavyrainshowersandthunder_polartwilight
            "heavysleet" -> R.drawable.heavysleet
            "heavysleetandthunder" -> R.drawable.heavysleetandthunder
            "heavysleetshowers_day" -> R.drawable.heavysleetshowers_day
            "heavysleetshowers_night" -> R.drawable.heavysleetshowers_night
            "heavysleetshowers_polartwilight" -> R.drawable.heavysleetshowers_polartwilight
            "heavysleetshowersandthunder_day" -> R.drawable.heavysleetshowersandthunder_day
            "heavysleetshowersandthunder_night" -> R.drawable.heavysleetshowersandthunder_night
            "heavysleetshowersandthunder_polartwilight" -> R.drawable.heavysleetshowersandthunder_polartwilight
            "heavysnow" -> R.drawable.heavysnow
            "heavysnowandthunder" -> R.drawable.heavysnowandthunder
            "heavysnowshowers_day" -> R.drawable.heavysnowshowers_day
            "heavysnowshowers_night" -> R.drawable.heavysnowshowers_night
            "heavysnowshowers_polartwilight" -> R.drawable.heavysnowshowers_polartwilight
            "heavysnowshowersandthunder_day" -> R.drawable.heavysnowshowersandthunder_day
            "heavysnowshowersandthunder_night" -> R.drawable.heavysnowshowersandthunder_night
            "heavysnowshowersandthunder_polartwilight" -> R.drawable.heavysnowshowersandthunder_polartwilight
            "lightrain" -> R.drawable.lightrain
            "lightrainandthunder" -> R.drawable.lightrainandthunder
            "lightrainshowers_day" -> R.drawable.lightrainshowers_day
            "lightrainshowers_night" -> R.drawable.lightrainshowers_night
            "lightrainshowers_polartwilight" -> R.drawable.lightrainshowers_polartwilight
            "lightrainshowersandthunder_day" -> R.drawable.lightrainshowersandthunder_day
            "lightrainshowersandthunder_night" -> R.drawable.lightrainshowersandthunder_night
            "lightrainshowersandthunder_polartwilight" -> R.drawable.lightrainshowersandthunder_polartwilight
            "lightsleet" -> R.drawable.lightsleet
            "lightsleetandthunder" -> R.drawable.lightsleetandthunder
            "lightsleetshowers_day" -> R.drawable.lightsleetshowers_day
            "lightsleetshowers_night" -> R.drawable.lightsleetshowers_night
            "lightsleetshowers_polartwilight" -> R.drawable.lightsleetshowers_polartwilight
            "lightsnow" -> R.drawable.lightsnow
            "lightsnowandthunder" -> R.drawable.lightsnowandthunder
            "lightsnowshowers_day" -> R.drawable.lightsnowshowers_day
            "lightsnowshowers_night" -> R.drawable.lightsnowshowers_night
            "lightsnowshowers_polartwilight" -> R.drawable.lightsnowshowers_polartwilight
            "lightssleetshowersandthunder_day" -> R.drawable.lightssleetshowersandthunder_day
            "lightssleetshowersandthunder_night" -> R.drawable.lightssleetshowersandthunder_night
            "lightssleetshowersandthunder_polartwilight" -> R.drawable.lightssleetshowersandthunder_polartwilight
            "lightssnowshowersandthunder_day" -> R.drawable.lightssnowshowersandthunder_day
            "lightssnowshowersandthunder_night" -> R.drawable.lightssnowshowersandthunder_night
            "lightssnowshowersandthunder_polartwilight" -> R.drawable.lightssnowshowersandthunder_polartwilight
            "partlycloudy_day" -> R.drawable.partlycloudy_day
            "partlycloudy_night" -> R.drawable.partlycloudy_night
            "partlycloudy_polartwilight" -> R.drawable.partlycloudy_polartwilight
            "rain" -> R.drawable.rain
            "rainandthunder" -> R.drawable.rainandthunder
            "rainshowers_day" -> R.drawable.rainshowers_day
            "rainshowers_night" -> R.drawable.rainshowers_night
            "rainshowers_polartwilight" -> R.drawable.rainshowers_polartwilight
            "rainshowersandthunder_day" -> R.drawable.rainshowersandthunder_day
            "rainshowersandthunder_night" -> R.drawable.rainshowersandthunder_night
            "rainshowersandthunder_polartwilight" -> R.drawable.rainshowersandthunder_polartwilight
            "sleet" -> R.drawable.sleet
            "sleetandthunder" -> R.drawable.sleetandthunder
            "sleetshowers_day" -> R.drawable.sleetshowers_day
            "sleetshowers_night" -> R.drawable.sleetshowers_night
            "sleetshowers_polartwilight" -> R.drawable.sleetshowers_polartwilight
            "sleetshowersandthunder_day" -> R.drawable.sleetshowersandthunder_day
            "sleetshowersandthunder_night" -> R.drawable.sleetshowersandthunder_night
            "sleetshowersandthunder_polartwilight" -> R.drawable.sleetshowersandthunder_polartwilight
            "snow" -> R.drawable.snow
            "snowandthunder" -> R.drawable.snowandthunder
            "snowshowers_day" -> R.drawable.snowshowers_day
            "snowshowers_night" -> R.drawable.snowshowers_night
            "snowshowers_polartwilight" -> R.drawable.snowshowers_polartwilight
            "snowshowersandthunder_day" -> R.drawable.snowshowersandthunder_day
            "snowshowersandthunder_night" -> R.drawable.snowshowersandthunder_night
            "snowshowersandthunder_polartwilight" -> R.drawable.snowshowersandthunder_polartwilight
            else -> {
                return R.drawable.image_not_availeable
            }
        }
    }

    fun clearWeatherCache() {
        isobaricDataCache.clear()
        weatherDataCache.clear()
    }
}

fun GridDatatype.sampleAtPosition(position: Position, layer: Int, time: Int = 0): Float {
    val i = coordinateSystem.findXYindexFromLatLon(position.latitude, position.longitude, null)
    val arr = readDataSlice(time, layer, i[1], i[0])
    return arr.getFloat(0)
}