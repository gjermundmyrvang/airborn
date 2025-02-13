package no.uio.ifi.in2000.team18.airborn.model.flightbrief

import no.uio.ifi.in2000.team18.airborn.data.entity.BuiltinAirport
import no.uio.ifi.in2000.team18.airborn.model.Position

data class MetarTaf(
    val metars: List<Metar>,
    val tafs: List<Taf>,
    val airport: Airport?,
) {
    val latestTaf
        get(): Taf? = if (tafs.isNotEmpty()) tafs.last() else null
    val latestMetar
        get(): Metar? = if (metars.isNotEmpty()) metars.last() else null
}


data class Airport(
    val icao: Icao,
    val name: String,
    val position: Position,
    val isFavourite: Boolean = false,
) {
    companion object {
        fun fromBuiltinAirport(airport: BuiltinAirport) =
            Airport(
                icao = Icao(airport.icao),
                name = airport.name,
                position = Position(airport.lat, airport.lon),
                isFavourite = airport.isfavourite,
            )
    }

    override fun toString() =
        "${position.latitude}N/${position.longitude}E" // TODO: support south and west
}

data class Icao(val code: String) {
    override fun toString(): String = code
}

data class Taf(val text: String) {
    override fun toString(): String = text
}

data class Sun(
    val sunrise: String, val sunset: String
)