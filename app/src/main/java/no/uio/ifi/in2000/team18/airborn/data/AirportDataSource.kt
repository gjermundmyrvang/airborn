package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import javax.inject.Inject

class AirportDataSource @Inject constructor() {
    // TODO: implement airports, this is maybe ok to hardcode
    suspend fun getByIcao(icao: Icao) =
        Airport(icao, name = "not implemented", position = Position(0.0, 0.0))
}