package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.AirportBrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Flightbrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import java.time.LocalDateTime
import javax.inject.Inject

class FlightbriefRepository @Inject constructor(
    val sigchartDataSource: SigchartDataSource,
    val turbulenceDataSource: TurbulenceDataSource,
    // All the data sources
) {
    val flightbriefs: HashMap<String, Flightbrief> = HashMap()

    fun getFlightbriefById(id: String): Flightbrief? = flightbriefs.getOrDefault(id, null)


    /**
     * @param to if this is null it means that it should be the same as from
     */
    suspend fun createFlightbrief(from: Icao, to: Icao?, time: LocalDateTime) {
        val brief = Flightbrief(
            departure = createAirportBrief(from, time),
            arrival = to?.let { createAirportBrief(it, time) }, // TODO: calculate arrival time
            altArrivals = listOf(),
            sigchart = sigchartDataSource.findSigchart(time)
        )
    }

    private suspend fun createAirportBrief(icao: Icao, time: LocalDateTime): AirportBrief =
        AirportBrief(
            airport = Airport(icao, "", Position(0.0, 0.0)), metarTaf = null, turbulence = null
        )

}