package no.uio.ifi.in2000.team18.airborn.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SigmetDatasource @Inject constructor(private val httpClient: HttpClient) {
    suspend fun fetchSigmets(): String = withContext(Dispatchers.IO) {
        httpClient.get("weatherapi/sigmets/2.0/").body()
    }
}