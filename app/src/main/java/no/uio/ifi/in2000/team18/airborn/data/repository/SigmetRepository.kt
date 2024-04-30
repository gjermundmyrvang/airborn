package no.uio.ifi.in2000.team18.airborn.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import no.uio.ifi.in2000.team18.airborn.data.datasource.SigmetDatasource
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseSigmets
import no.uio.ifi.in2000.team18.airborn.model.Sigmet
import javax.inject.Inject

class SigmetRepository @Inject constructor(private val dataSource: SigmetDatasource) {
    private val sigmetMutex = Mutex()
    private var sigmetDataCache: List<Sigmet> = listOf()
    suspend fun fetchSigmets(): List<Sigmet> {
        if (sigmetDataCache.isEmpty()) {
            val sigmets = parseSigmets(dataSource.fetchSigmets())
            sigmetMutex.withLock { this.sigmetDataCache = sigmets }
        }
        return sigmetMutex.withLock { this.sigmetDataCache }
    }
}