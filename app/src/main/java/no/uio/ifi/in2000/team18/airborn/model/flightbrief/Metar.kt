package no.uio.ifi.in2000.team18.airborn.model.flightbrief

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.PhenomenonDescriptor
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.PhenomenonExtra
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.PhenomenonObscuration
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.PhenomenonPrecipitation
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.PhenomenonQualifier
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Pressure
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.Temperature

data class Metar(
    val station: Icao,
    val time: MetarDateTime,
    val wind: Pair<MetarWind, Pair<Direction, Direction>?>,
    val cav: Cav,
    val temperatures: Pair<Temperature, Temperature>,
    val altimeterSetting: Pressure,
    val rest: String,
    val text: String = "",
    val downloaded: Instant?,
) {
    val instant: Instant? get() = downloaded?.let { time.resolveInstant(it) }
}

data class MetarDateTime(val day: Int, val hour: Int, val minute: Int) {
    override fun toString(): String = "$day. $hour:$minute"

    fun resolveInstant(
        downloaded: Instant
    ) = downloaded.toLocalDateTime(TimeZone.UTC).let {
        LocalDateTime(
            it.year,
            it.month - if (day > it.dayOfMonth) 1 else 0,
            day, hour, minute,
        ).toInstant(TimeZone.UTC)
    }
}

sealed interface VisibilityDistance {
    data object NoVisibility : VisibilityDistance {
        override fun toString() = "< 50 m"
    }

    data object Clear : VisibilityDistance {
        override fun toString() = "≥ 10 km"
    }

    data class Distance(val distance: Int) : VisibilityDistance {
        // TODO: Correct Unit
        override fun toString() = "$distance m"
    }
}

sealed interface Cav {
    data object OK : Cav
    data class Info(
        val visibility: Pair<VisibilityDistance, List<Pair<VisibilityDistance, Direction>>?>,
        val rvrs: List<Rvr>,
        val weatherPhenomena: List<WeatherPhenomenon>,
        val clouds: Clouds
    ) : Cav
}

data class WeatherPhenomenon(
    val qualifier: PhenomenonQualifier,
    val descriptor: PhenomenonDescriptor?,
    val precipitation: List<PhenomenonPrecipitation>,
    val obscuration: List<PhenomenonObscuration>,
    val other: List<PhenomenonExtra>,
    val inVicinity: Boolean,
) {
    override fun toString() = buildString {
        if (descriptor == PhenomenonDescriptor.Thunderstorm) {
            append("thunderstorm with ")
            if (qualifier != PhenomenonQualifier.Moderate) {
                append(qualifier)
                append(" ")
            }
            append(formatList(precipitation.map { it.toString() } + obscuration.map { it.toString() } + other.map { it.toString() }))
        } else {
            append("$qualifier")
            descriptor?.let { append(" $it") }
            precipitation.forEach { append(" $it") }

            if (precipitation.isNotEmpty() && (other.isNotEmpty() || obscuration.isNotEmpty())) append(
                " with"
            )
            when {
                other.isNotEmpty() && obscuration.isNotEmpty() -> {
                    append(" ${formatList(obscuration.map { it.toString() })},")
                    append(" and ${formatList(other.map { it.toString() })}")
                }

                obscuration.isNotEmpty() -> append(" ${formatList(obscuration.map { it.toString() })}")
                other.isNotEmpty() -> append(" ${formatList(other.map { it.toString() })}")
            }
        }
        if (inVicinity) append(" in the vicinity")
    }
}

fun formatList(strings: List<String>): String = when (strings.size) {
    0 -> ""; 1 -> strings[0]
    else -> strings.subList(0, strings.size - 1).joinToString {
        it
    } + " and ${strings.last()}"
}

sealed interface MetarWindDirection {
    data object Variable : MetarWindDirection {
        override fun toString(): String = "Variable"
    }

    data class Constant(val direction: Direction) : MetarWindDirection {
        override fun toString() = direction.toString()
    }

}

data class MetarWind(
    val direction: MetarWindDirection,
    val speed: Speed,
    val gustSpeed: Speed?,
)

sealed interface RvrVisibility {
    data class MoreThan(val distance: Distance) : RvrVisibility {
        override fun toString() = "≥ $distance"
    }

    data class LessThan(val distance: Distance) : RvrVisibility {
        override fun toString() = "< $distance"
    }

    data class Exactly(val distance: Distance) : RvrVisibility {
        override fun toString() = "$distance"
    }

    data class Range(val min: Distance, val max: Distance) : RvrVisibility {
        override fun toString() = "$min to $max"
    }
}

enum class RvrTrend {
    Downward, Upward, NoChange,
}

data class Rvr(
    val runway: String,
    val visibility: RvrVisibility,
    val trend: RvrTrend?,
)

enum class SkyCover {
    FEW, SCT, BKN, VV, OVC, Unknown, ;

    override fun toString() = when (this) {
        FEW -> "few"
        SCT -> "scattered"
        BKN -> "broken"
        VV -> "vertical visibility"
        OVC -> "overcast"
        Unknown -> "unknown" // was this below radar or something
    }
}

enum class CloudType {
    Cumulus, // "CU"
    ToweringCumulus, // "TCU"
    Unknown, // "///"
    Nothing, // "";
    Cumulonimbus; // CB

    override fun toString() = when (this) {
        Cumulus -> "cumulus"
        ToweringCumulus -> "towering cumulus"
        Unknown -> "unknown"
        Nothing -> "no information"
        Cumulonimbus -> "cumulonimbus"
    }
}

data class CloudLayer(
    val cover: SkyCover,
    val height: Distance,
    val type: CloudType,
)

sealed interface Clouds {
    data class Layers(val layers: List<CloudLayer>) : Clouds
    data object NCD : Clouds
    data object NSC : Clouds
}
