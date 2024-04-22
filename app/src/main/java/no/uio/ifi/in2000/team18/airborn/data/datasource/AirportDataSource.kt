package no.uio.ifi.in2000.team18.airborn.data.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team18.airborn.data.dao.BuiltinAirportDao
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import javax.inject.Inject

class AirportDataSource @Inject constructor(
    private val builtinAirportDao: BuiltinAirportDao
) {
    suspend fun getByIcao(icao: Icao): Airport? = withContext(Dispatchers.IO) {
        builtinAirportDao.getByIcao(icao.code)?.let {
            Airport(icao = Icao(it.icao), name = it.name, position = Position(it.lat, it.lon))
        }
    }

    suspend fun search(query: String): List<Airport> = withContext(Dispatchers.IO) {
        builtinAirportDao.search(query).map {
            Airport(
                icao = Icao(it.icao),
                name = it.name,
                position = Position(latitude = it.lat, longitude = it.lon)
            )
        }
    }

    suspend fun all(): List<Airport> = search("")
}