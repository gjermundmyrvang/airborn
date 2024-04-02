package no.uio.ifi.in2000.team18.airborn.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.LocationData
import no.uio.ifi.in2000.team18.airborn.model.flightBrief.Airport
import javax.inject.Inject

class LocationForecastDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchForecast(airport: Airport): LocationData {
        return client.get("weatherapi/locationforecast/2.0/compact?lat=${airport.position.latitude}&lon=${airport.position.longitude}")
            .body()
    }
}
