package no.uio.ifi.in2000.team18.airborn.data.repository

import no.uio.ifi.in2000.team18.airborn.data.datasource.SigmetDatasource
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseSigmets
import no.uio.ifi.in2000.team18.airborn.model.Sigmet
import javax.inject.Inject

class SigmetRepository @Inject constructor(private val dataSource: SigmetDatasource) {
    suspend fun fetchSigmets(): List<Sigmet> {
        val source = dataSource.fetchSigmets()
        return parseSigmets(source)
    }
}