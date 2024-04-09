package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.CloudFraction
import no.uio.ifi.in2000.team18.airborn.model.Details
import no.uio.ifi.in2000.team18.airborn.model.DirectionInDegrees
import no.uio.ifi.in2000.team18.airborn.model.FogAreaFraction
import no.uio.ifi.in2000.team18.airborn.model.Hpa
import no.uio.ifi.in2000.team18.airborn.model.Humidity
import no.uio.ifi.in2000.team18.airborn.model.NextHourDetails
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.Temperature
import no.uio.ifi.in2000.team18.airborn.model.TimeSeries
import no.uio.ifi.in2000.team18.airborn.model.UvIndex
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherDetails
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import javax.inject.Inject

class LocationForecastRepository @Inject constructor(private val locationForecastDataSource: LocationForecastDataSource) {
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

    /*private val bokmål = mapOf(
        "clearsky" to "Klarvær",
        "fair" to "Lettskyet",
        "partlycloudy" to "Delvis skyet",
        "cloudy" to "Skyet",
        "lightrainshowers" to "Lette regnbyger",
        "rainshowers" to "Regnbyger",
        "heavyrainshowers" to "Kraftige regnbyger",
        "lightrainshowersandthunder" to "Lette regnbyger og torden",
        "rainshowersandthunder" to "Regnbyger og torden",
        "heavyrainshowersandthunder" to "Kraftige regnbyger og torden",
        "lightsleetshowers" to "Lette sluddbyger",
        "sleetshowers" to "Sluddbyger",
        "heavysleetshowers" to "Kraftige sluddbyger",
        "lightssleetshowersandthunder " to "Lette sluddbyger og torden",
        "sleetshowersandthunder" to "Sluddbyger og torden",
        "heavysleetshowersandthunder" to "Kraftige sluddbyger og torden",
        "lightsnowshowers" to "Lette snøbyger",
        "snowshowers" to "Snøbyger",
        "heavysnowshowers" to "Kraftige snøbyger",
        "lightssnowshowersandthunder" to "Lette snøbyger og torden",
        "snowshowersandthunder" to "Snøbyger og torden",
        "heavysnowshowersandthunder" to "Kraftige snøbyger og torden",
        "lightrain" to "Lett regn",
        "rain" to "Regn",
        "heavyrain" to "Kraftig regn",
        "lightrainandthunder" to "Lett regn og torden",
        "rainandthunder" to "Regn og torden",
        "heavyrainandthunder" to "Kraftig regn og torden",
        "lightsleet" to "Lett sludd",
        "sleet" to "Sludd",
        "heavysleet" to "Kraftig sludd",
        "lightsleetandthunder" to "Lett sludd og torden",
        "sleetandthunder" to "Sludd og torden",
        "heavysleetandthunder" to "Kraftig sludd og torden",
        "lightsnow" to "Lett snø",
        "snow" to "Snø",
        "heavysnow" to "Kraftig snø",
        "lightsnowandthunder" to "Lett snø og torden",
        "snowandthunder" to "Snø og torden",
        "heavysnowandthunder" to "Kraftig snø og torden",
        "fog" to "Tåke",
    )
     */

    /*
    private val nynorsk = mapOf(
        "clearsky" to "Klårvêr",
        "fair" to "Lettskya",
        "partlycloudy" to "Delvis skya",
        "cloudy" to "Skya",
        "lightrainshowers" to "Lette regnbyer",
        "rainshowers" to "Regnbyer",
        "heavyrainshowers" to "Kraftige regnbyer",
        "lightrainshowersandthunder" to "Lette regnbyer og torevêr",
        "rainshowersandthunder" to "Regnbyer og torevêr",
        "heavyrainshowersandthunder" to "Kraftige regnbyer og torevêr",
        "lightsleetshowers" to "Lette sluddbyer",
        "sleetshowers" to "Sluddbyer",
        "heavysleetshowers" to "Kraftige sluddbyer",
        "lightssleetshowersandthunder " to "Lette sluddbyer og torevêr",
        "sleetshowersandthunder" to "Sluddbyer og torevêr",
        "heavysleetshowersandthunder" to "Kraftige sluddbyer og torevêr ",
        "lightsnowshowers" to "Lette snøbyer",
        "snowshowers" to "Snøbyer",
        "heavysnowshowers" to "Kraftige snøbyer",
        "lightssnowshowersandthunder" to "Lette snøbyer og torevêr",
        "snowshowersandthunder" to "Snøbyer og torevêr",
        "heavysnowshowersandthunder" to "Kraftige snøbyer og torevêr",
        "lightrain" to "Lett regn",
        "rain" to "Regn",
        "heavyrain" to "Kraftig regn",
        "lightrainandthunder" to "Lett regn og torevêr",
        "rainandthunder" to "Regn og torevêr",
        "heavyrainandthunder" to "Kraftig regn og torevêr",
        "lightsleet" to "Lett sludd",
        "sleet" to "Sludd",
        "heavysleet" to "Kraftig sludd",
        "lightsleetandthunder" to "Lett sludd og torevêr",
        "sleetandthunder" to "Sludd og torevêr",
        "heavysleetandthunder" to "Kraftig sludd og torevêr",
        "lightsnow" to "Lett snø",
        "snow" to "Snø",
        "heavysnow" to "Kraftig snø",
        "lightsnowandthunder" to "Lett snø og torevêr",
        "snowandthunder" to "Snø og torevêr",
        "heavysnowandthunder" to "Kraftig snø og torevêr",
        "fog" to "Skodde",
    )
     */

    suspend fun getWeatherDays(airport: Airport): List<WeatherDay> {
        val weatherData = locationForecastDataSource.fetchForecast(airport).properties.timeseries
        return mapToWeatherDay(weatherData)
    }

    private fun mapToWeatherDay(timeseries: List<TimeSeries>): List<WeatherDay> {
        val groupedByDate = timeseries.groupBy { DateTime(isoDateTime = it.time).date }
        return groupedByDate.map { (_, timeSeriesList) ->
            WeatherDay(date = DateTime(isoDateTime = timeSeriesList.first().time),
                weather = timeSeriesList.map { timeSeries ->
                    WeatherHour(
                        time = DateTime(isoDateTime = timeSeries.time).time,
                        weatherDetails = mapDetailsToWeatherDetails(timeSeries.data.instant.details),
                        nextOneHour = if (timeSeries.data.next_1_hours != null) english[timeSeries.data.next_1_hours.summary.symbol_code.substringBefore(
                            "_"
                        )]?.let {
                            NextHourDetails(
                                symbol_code = it,
                                icon = iconMapper(timeSeries.data.next_1_hours.summary.symbol_code),
                                chanceOfRain = timeSeries.data.next_1_hours.details["precipitation_amount"]
                            )
                        } else null,
                        nextSixHour = if (timeSeries.data.next_6_hours != null) english[timeSeries.data.next_6_hours.summary.symbol_code.substringBefore(
                            "_"
                        )]?.let {
                            NextHourDetails(
                                symbol_code = it,
                                icon = iconMapper(timeSeries.data.next_6_hours.summary.symbol_code),
                                chanceOfRain = timeSeries.data.next_6_hours.details["precipitation_amount"]
                            )
                        } else null,
                        nextTwelweHour = if (timeSeries.data.next_12_hours != null) english[timeSeries.data.next_12_hours.summary.symbol_code.substringBefore(
                            "_"
                        )]?.let {
                            NextHourDetails(
                                symbol_code = it,
                                icon = iconMapper(timeSeries.data.next_12_hours.summary.symbol_code),
                                chanceOfRain = timeSeries.data.next_12_hours.details["precipitation_amount"]
                            )
                        } else null,
                    )
                })
        }
    }

    private fun mapDetailsToWeatherDetails(details: Details): WeatherDetails {
        return WeatherDetails(
            airPressureSeaLevel = Hpa(details.air_pressure_at_sea_level),
            airTemperature = Temperature(details.air_temperature),
            cloudFraction = CloudFraction(
                cloudFraction = details.cloud_area_fraction,
                cloudFractionHigh = details.cloud_area_fraction_high,
                cloudFractionMedium = details.cloud_area_fraction_medium,
                cloudFractionLow = details.cloud_area_fraction_low
            ),
            humidity = Humidity(details.relative_humidity),
            windDirection = DirectionInDegrees(details.wind_from_direction),
            windSpeed = Speed(details.wind_speed),
            dewPointTemperature = Temperature(details.dew_point_temperature),
            fogAreaFraction = FogAreaFraction(details.fog_area_fraction),
            uvIndex = UvIndex(details.ultraviolet_index_clear_sky)
        )
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