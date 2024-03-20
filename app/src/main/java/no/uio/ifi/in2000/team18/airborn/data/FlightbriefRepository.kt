package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.AirportBrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Flightbrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class FlightbriefRepository constructor(
    val sigchartDataSource: SigchartDataSource,
    val turbulenceDataSource: TurbulenceDataSource,
    val tafmetarDataSource: TafmetarDataSource,
    val airportDataSource: AirportDataSource,
    val isobaricRepository: IsobaricRepository
    // All the data sources
) {
    val flightbriefs: ConcurrentHashMap<String, Flightbrief> = ConcurrentHashMap()

    fun getFlightbriefById(id: String): Flightbrief? = flightbriefs.getOrDefault(id, null)


    /**
     * @param to if this is null it means that it should be the same as from
     */
    suspend fun createFlightbrief(from: Icao, to: Icao?, time: LocalDateTime): String {
        val brief = Flightbrief(
            departure = createAirportBrief(from, time),
            arrival = to?.let { createAirportBrief(it, time) }, // TODO: calculate arrival time
            altArrivals = listOf(),
            sigchart = sigchartDataSource.findSigchart(time)
        )

        val id = UUID.randomUUID()
            .toString() // This is always unique. There are more uuids than atoms in the observable universe
        flightbriefs[id] = brief
        return id
    }

    // TODO: change time format?
    private suspend fun createAirportBrief(icao: Icao, time: LocalDateTime): AirportBrief =
        AirportBrief(
            airport = airportDataSource.getByIcao(icao)!!,
            metarTaf = createMetarTaf(icao.code),
            turbulence = turbulenceDataSource.createTurbulence(icao),
            isobaric = isobaricRepository.getIsobaricData
                (
                airportDataSource.getByIcao(icao)!!.position,
                time = "A date and a time"
            )
        )

    private suspend fun createMetarTaf(icao: String): MetarTaf {
        return tafmetarDataSource.fetchTafMetar(icao)
    }


}