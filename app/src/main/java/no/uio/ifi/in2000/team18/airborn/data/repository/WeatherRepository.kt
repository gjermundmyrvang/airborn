package no.uio.ifi.in2000.team18.airborn.data.repository

import android.util.Log
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.data.datasource.GribDataSource
import no.uio.ifi.in2000.team18.airborn.data.datasource.LocationForecastDataSource
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.GribFile
import no.uio.ifi.in2000.team18.airborn.model.GribFiles
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
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricDataPoint
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricLayer
import no.uio.ifi.in2000.team18.airborn.model.mps
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import no.uio.ifi.in2000.team18.airborn.ui.common.toSystemZoneOffset
import ucar.nc2.dt.GridDatatype
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

const val TEMPERATURE_LAPSE_RATE: Double = 0.0065 // in (K/m)
const val PRESSURE_CALCULATION_EXPONENT: Double = 1 / 5.25579


class WeatherRepository @Inject constructor(
    private val locationForecastDataSource: LocationForecastDataSource,
    private val gribDataSource: GribDataSource,
) {
    suspend fun fetchIsobaricDatapoint(): GribFiles {
        return gribDataSource.availableGribFiles()
    }

    suspend fun currentGribFile(gribFiles: GribFiles): GribFile {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        return gribFiles.find {
            it.params.time.isBefore(now) || it.params.time.isEqual(now) && it.params.time.plusHours(
                3
            ).isAfter(
                now
            )
        } ?: gribFiles.last()
    }

    suspend fun fetchIsobaricData(gribFile: GribFile, position: Position): IsobaricData {
        val windsAloft = gribDataSource.useGribFile(gribFile) { dataset ->
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
            }.toMap()
        }

        val layers = windsAloft.map { (key, value) ->
            val layer = IsobaricLayer(
                pressure = Pressure(key.toDouble()),
                temperature = Temperature(value[0] - 273.15),
                uWind = value[1],
                vWind = value[2]
            )
            layer.windFromDirection = Direction.fromWindUV(layer.uWind, layer.vWind)
            layer.windSpeed = calculateWindSpeed(layer.uWind, layer.vWind)
            layer.height = Distance(calculateHeight(layer))
            layer
        }.filter {
            val h = it.height
            val maxHeight = 15000
            val result = if (h != null) (h.feet <= maxHeight) else false
            result
        }
        return IsobaricData(
            position, gribFile.params.time.toSystemZoneOffset(), layers
        )
    }


    suspend fun getRouteIsobaric(departure: Airport, arrival: Airport): RouteIsobaric {
        val gribFiles = fetchIsobaricDatapoint()

        val distance = departure.position.distanceTo(arrival.position)
        val bearing = departure.position.bearingTo(arrival.position)

        return RouteIsobaric(
            departure = departure,
            arrival = arrival,
            timeSeries = gribFiles.groupBy { it.params.time },
            isobaric = ,
            distance = distance,
            bearing = bearing
        )
    }

    private fun calculateWindSpeed(uWind: Double, vWind: Double): Speed =
        (sqrt(uWind.pow(2) + vWind.pow(2))).mps

    /**
     * Calculate height of isobaric layer
     *
     * @param refTemperature todo: what is this
     * @param pressureLevelZero the pressure at sea level
     */
    fun calculateHeight(
        layer: IsobaricLayer,
        refTemperature: Double = 288.15,
        pressureLevelZero: Double = 1013.25,
    ) = refTemperature * (1 - (layer.pressure.toDouble() / pressureLevelZero).pow(
        PRESSURE_CALCULATION_EXPONENT
    )) / TEMPERATURE_LAPSE_RATE

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

    suspend fun getWeatherDays(airport: Airport): List<WeatherDay> {
        val weatherData =
            locationForecastDataSource.fetchForecast(airport.position).properties.timeseries
        return mapToWeatherDay(weatherData)
    }

    private fun mapToWeatherDay(timeseries: List<TimeSeries>): List<WeatherDay> {
        val groupedByDate = timeseries.groupBy { DateTime(isoDateTime = it.time).date }
        return groupedByDate.map { (_, timeSeriesList) ->
            WeatherDay(date = DateTime(isoDateTime = timeSeriesList.first().time),
                weather = timeSeriesList.map { timeSeries ->
                    WeatherHour(
                        time = DateTime(isoDateTime = timeSeries.time).time,
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
}

fun GridDatatype.sampleAtPosition(position: Position, layer: Int, time: Int = 0): Float {
    val i = coordinateSystem.findXYindexFromLatLon(position.latitude, position.longitude, null)
    val arr = readDataSlice(time, layer, i[1], i[0])
    return arr.getFloat(0)
}