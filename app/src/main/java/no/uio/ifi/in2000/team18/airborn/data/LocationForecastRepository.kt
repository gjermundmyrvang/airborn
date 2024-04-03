package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.TimeSeries
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class LocationForecastRepository @Inject constructor(private val locationForecastDataSource: LocationForecastDataSource) {
    suspend fun getWeatherDays(airport: Airport): List<WeatherDay> {
        val weatherData = locationForecastDataSource.fetchForecast(airport).properties.timeseries
        return mapToWeatherDay(weatherData)
    }

    private fun mapToWeatherDay(timeseries: List<TimeSeries>): List<WeatherDay> {
        val formatter = DateTimeFormatter.ofPattern("EEEE dd. MMMM", Locale("no"))
        val groupedByDate = timeseries.groupBy { ZonedDateTime.parse(it.time).toLocalDate() }
        return groupedByDate.map { (date, timeSeriesList) ->
            WeatherDay(
                date = date.format(formatter).toString(),
                weather = timeSeriesList.map { timeSeries ->
                    WeatherHour(
                        hour = ZonedDateTime.parse(timeSeries.time).hour,
                        weatherDetails = timeSeries.data.instant.details,
                        next_12_hours = timeSeries.data.next_12_hours,
                        next_1_hours = timeSeries.data.next_1_hours,
                        next_6_hours = timeSeries.data.next_6_hours,
                        icon_1_hour = timeSeries.data.next_1_hours?.summary?.let { iconMapper(it.symbol_code) },
                        icon_6_hour = timeSeries.data.next_6_hours?.summary?.let { iconMapper(it.symbol_code) },
                        icon_12_hour = timeSeries.data.next_12_hours?.summary?.let { iconMapper(it.symbol_code) },
                    )
                }
            )
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
                return R.drawable.ic_launcher_foreground
            }
        }
    }
}