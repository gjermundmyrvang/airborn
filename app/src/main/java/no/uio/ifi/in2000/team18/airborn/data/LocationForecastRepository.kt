package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.model.TimeSeries
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import java.time.ZonedDateTime

class LocationForecastRepository(private val locationForecastDataSource: LocationForecastDataSource) { // Will eventually use Dagger&Hilt

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

suspend fun main() {
    val data = LocationForecastRepository(LocationForecastDataSource())
    val airport = Airport(
        icao = Icao("ENBN"),
        name = "Brønnøysund airport, Brønnøy",
        position = Position(60.1, 9.58)
    )
    println(data.getWeatherDays(airport))
}