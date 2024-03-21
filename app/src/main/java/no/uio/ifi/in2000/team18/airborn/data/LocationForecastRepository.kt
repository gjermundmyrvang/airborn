package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.model.TimeSeries
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import java.time.ZonedDateTime
import javax.inject.Inject

class LocationForecastRepository @Inject constructor(val locationForecastDataSource: LocationForecastDataSource) {
    suspend fun getWeatherDays(airport: Airport): List<WeatherDay> {
        val weatherData = locationForecastDataSource.fetchForecast(airport).properties.timeseries
        return mapToWeatherDay(weatherData)
    }

    private fun mapToWeatherDay(timeseries: List<TimeSeries>): List<WeatherDay> {
        val groupedByDate = timeseries.groupBy { ZonedDateTime.parse(it.time).toLocalDate() }
        return groupedByDate.map { (date, timeSeriesList) ->
            WeatherDay(
                date = date.toString(),
                weather = timeSeriesList.map { timeSeries ->
                    WeatherHour(
                        hour = ZonedDateTime.parse(timeSeries.time).hour,
                        weatherDetails = timeSeries.data.instant.details,
                        next_12_hours = timeSeries.data.next_12_hours,
                        next_1_hours = timeSeries.data.next_1_hours,
                        next_6_hours = timeSeries.data.next_6_hours
                    )
                }
            )
        }
    }
}