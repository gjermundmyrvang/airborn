package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.Radar
import javax.inject.Inject


class RadarDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchRadarAnimations(): List<Radar> =
        client.get("weatherapi/radar/2.0/available.json?content=animation").body()
}