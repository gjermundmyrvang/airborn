package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.team18.airborn.model.Webcam
import no.uio.ifi.in2000.team18.airborn.model.WebcamResponse
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport

class WebcamDataSource {
    private var radius: Int = 50
    private val apiKey = "titERHFlGOLQGIILEUnufyWHQYcAlbDO"
    val client: HttpClient = HttpClient(CIO) {
        defaultRequest {
            header("x-windy-api-key", apiKey)
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchImage(airport: Airport): List<Webcam> {
        val response: WebcamResponse =
            client.get("https://api.windy.com/webcams/api/v3/webcams?lang=en&limit=10&offset=0&nearby=${airport.position.latitude}%2C${airport.position.longitude}%2C$radius&include=images")
                .body()
        return response.webcams
    }

    fun increaseRadius(increment: Int) {
        if (radius + increment > 200) {
            return
        }
        radius += increment
    }
}