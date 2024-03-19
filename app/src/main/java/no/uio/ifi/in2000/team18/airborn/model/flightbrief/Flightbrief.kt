package no.uio.ifi.in2000.team18.airborn.model.flightbrief

import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.Turbulence

data class Flightbrief(
    val departure: AirportBrief,
    val arrival: AirportBrief?,
    val altArrivals: List<AirportBrief>,
    val sigchart: Sigchart,

    )

data class AirportBrief(
    val airport: Airport,
    val metarTaf: MetarTaf?,
    val turbulence: TurbulenceMapAndCross?,
)


data class MetarTaf(
    val metars: List<Metar>,
    val tafs: List<Taf>,
) {
    val latestTaf
        get(): Taf? = if (tafs.isNotEmpty()) tafs.last() else null
    val latestMetar
        get(): Metar? = if (metars.isNotEmpty()) metars.last() else null
}

data class TurbulenceMapAndCross(
    val map: List<Turbulence>,
    val crossSection: List<Turbulence>
)

data class Airport(
    val icao: Icao,
    val name: String,
    val position: Position,
)

data class Position(
    val latitude: Double,
    val longitude: Double
)

data class Icao(val code: String){
    override fun toString(): String = code
}
data class Metar(val text: String) {
    override fun toString(): String = text
}
data class Taf(val text: String){
    override fun toString(): String = text
}