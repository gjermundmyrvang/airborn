package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import javax.inject.Inject


class SigchartDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchSigcharts(): List<Sigchart> {
        val res = client.get("weatherapi/sigcharts/2.0/available.json")
        return res.body()
    }
}