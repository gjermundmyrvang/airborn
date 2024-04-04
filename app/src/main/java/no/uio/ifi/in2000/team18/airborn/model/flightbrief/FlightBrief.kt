package no.uio.ifi.in2000.team18.airborn.model.flightbrief

import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.Turbulence
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import java.time.ZonedDateTime

data class FlightBrief(
    val departure: AirportBrief,
    val arrival: AirportBrief?,
    val altArrivals: List<AirportBrief>,
    val sigchart: Sigchart,
)

data class AirportBrief(
    val airport: Airport,
    val metarTaf: MetarTaf?,
    val turbulence: TurbulenceMapAndCross?,
    val isobaric: IsobaricData?,
    val weather: List<WeatherDay>,
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
) {

    val mapDict
        get(): Map<ZonedDateTime, Turbulence> = map.map { ZonedDateTime.parse(it.params.time) to it }
            .toMap()

    val crossSectionDict
        get(): Map<ZonedDateTime, Turbulence> = crossSection.map { ZonedDateTime.parse(it.params.time) to it }
            .toMap()

    fun currentTurbulenceTime(): ZonedDateTime {
        val time = ZonedDateTime.now(ZoneOffset.UTC).let {
            if (it.minute > 30) it.plusHours(1) else it
        }
        val formattedTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00'Z'"))
        return ZonedDateTime.parse(formattedTime)
    }

    fun allTurbulenceTimes(): Map<String, List<ZonedDateTime>>? {
        val turbulenceTimes = if (map.isNotEmpty()) {
            map.map { ZonedDateTime.parse(it.params.time) }
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
    val latitude: Double,
    val longitude: Double
)

data class Icao(val code: String) {
    override fun toString(): String = code
}

data class Metar(val text: String) {
    override fun toString(): String = text
}

data class Taf(val text: String) {
    override fun toString(): String = text
}