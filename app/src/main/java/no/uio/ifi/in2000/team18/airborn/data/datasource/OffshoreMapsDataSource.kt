package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.OffshoreMap
import javax.inject.Inject

class OffshoreMapsDataSource @Inject constructor(val client: HttpClient) {

    suspend fun fetchOffshoreMaps(): List<OffshoreMap> =
        client.get("weatherapi/offshoremaps/1.0/available.json").body()
}