package no.uio.ifi.in2000.team18.airborn.data.datasource

import android.util.Log
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

    suspend fun getAirportsNearby(airport: Airport, max: Int) = withContext(Dispatchers.IO) {
        builtinAirportDao.getAirportsNearby(
            airport.position.latitude,
            airport.position.longitude,
            max
        )
    }

    suspend fun search(query: String) = withContext(Dispatchers.IO) {
        builtinAirportDao.search(query)
    }

    suspend fun all() = search("")
    suspend fun addFaovourite(icao: Icao) = withContext(Dispatchers.IO) {
        Log.d("favourite", "Adding $icao to favourites")
        builtinAirportDao.addFavourite(icao.code)
    }

    suspend fun removeFavourite(icao: Icao) = withContext(Dispatchers.IO) {
        Log.d("favourite", "Adding $icao to favourites")
        builtinAirportDao.removeFavourite(icao.code)
    }
}