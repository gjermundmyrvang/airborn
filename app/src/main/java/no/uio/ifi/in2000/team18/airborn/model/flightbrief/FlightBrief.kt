package no.uio.ifi.in2000.team18.airborn.model.flightbrief

import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team18.airborn.model.Position

data class MetarTaf(
    val metars: List<Metar>,
    val tafs: List<Taf>,
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
)

data class Position(
    val latitude: Double, val longitude: Double
) {
    fun toPoints(): Point = Point.fromLngLat(longitude, latitude)
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
