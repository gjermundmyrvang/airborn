package no.uio.ifi.in2000.team18.airborn.model.flightbrief

import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team18.airborn.model.Turbulence
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
    val map: List<Turbulence>, val crossSection: List<Turbulence>
) {

    val mapDict: Map<ZonedDateTime, String>? = if (map.isNotEmpty()) {
        map.associate {
            ZonedDateTime.parse(it.params.time)
                .withZoneSameInstant(ZoneId.systemDefault()) to it.uri
        }
    } else {
        null
    }

    val crossSectionDict: Map<ZonedDateTime, String>? = if (crossSection.isNotEmpty()) {
        crossSection.associate {
            ZonedDateTime.parse(it.params.time)
                .withZoneSameInstant(ZoneId.systemDefault()) to it.uri
        }
    } else {
        null
    }

    fun currentTurbulenceTime(): ZonedDateTime {
        val time = ZonedDateTime.now(ZoneOffset.UTC).let {
            if (it.minute > 30) it.plusHours(1) else it
        }
        val formattedTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00'Z'"))
        return ZonedDateTime.parse(formattedTime).withZoneSameInstant(ZoneId.systemDefault())
    }

    fun allTurbulenceTimes(): Map<String, List<ZonedDateTime>>? {
        val turbulenceTimes = if (map.isNotEmpty()) {
            map.map {
                ZonedDateTime.parse(it.params.time).withZoneSameInstant(ZoneId.systemDefault())
            }
        } else {
            null
        }

        return turbulenceTimes?.groupBy { time ->
            time.dayOfWeek.name
        } ?: return null
    }
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
