package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.LocationData
import no.uio.ifi.in2000.team18.airborn.model.Position
import javax.inject.Inject

class LocationForecastDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchForecast(position: Position): LocationData {
        return client.get("weatherapi/locationforecast/2.0/complete?lat=${position.latitude}&lon=${position.longitude}")
            .body()
    }
}