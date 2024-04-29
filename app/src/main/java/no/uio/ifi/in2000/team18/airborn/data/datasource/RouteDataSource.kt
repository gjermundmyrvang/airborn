package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.RouteForecast
import javax.inject.Inject

class RouteDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchRoute(route: String): List<RouteForecast> =
        client.get("weatherapi/routeforecast/2.0/available.json?route=$route").body()

    suspend fun fetchAllAvailableRoutes(): List<String> =
        client.get("weatherapi/routeforecast/2.0/routes").body()
}