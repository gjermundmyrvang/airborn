package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class SunriseSunsetDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchSunriseSunset(lat: Int, lon: Int) {
        return client.get("weatherapi/sunrise/3.0/sun?lat=$lat&lon=$lon")
            .body()
    }
}