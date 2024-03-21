package no.uio.ifi.in2000.team18.airborn.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.team18.airborn.model.LocationData
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position

class LocationForecastDataSource {
    val client: HttpClient = HttpClient(CIO) {
        defaultRequest {
            header("X-Gravitee-API-Key", "95d6f7c0-9b84-4002-89ba-483ac0f827c6")
            url("https://gw-uio.intark.uh-it.no/in2000/")
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchForecast(airport: Airport): LocationData {
        return client.get("weatherapi/locationforecast/2.0/compact?lat=${airport.position.latitude}&lon=${airport.position.longitude}")
            .body()
    }
}

suspend fun main() {
    val locationForecastDataSource = LocationForecastDataSource()
    val airport = Airport(
        icao = Icao("ENBN"),
        name = "Brønnøysund airport, Brønnøy",
        position = Position(60.1, 9.58)
    )
    println(locationForecastDataSource.fetchForecast(airport = airport))
}