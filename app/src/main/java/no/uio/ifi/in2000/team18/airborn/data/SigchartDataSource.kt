package no.uio.ifi.in2000.team18.airborn.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import java.time.LocalDateTime
import javax.inject.Inject


class SigchartDataSource @Inject constructor(val client: HttpClient) {
    suspend fun fetchSigcharts(): List<Sigchart> {
        val res = client.get("weatherapi/sigcharts/2.0/available.json")
        return res.body()
    }

    suspend fun findSigchart(time: LocalDateTime): Sigchart {
        return fetchSigcharts().last() // TODO: find correct sigchart
    }
}