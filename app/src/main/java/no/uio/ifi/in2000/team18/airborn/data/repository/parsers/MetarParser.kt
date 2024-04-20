package no.uio.ifi.in2000.team18.airborn.data.repository.parsers

import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Pressure
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.Temperature
import no.uio.ifi.in2000.team18.airborn.model.celsius
import no.uio.ifi.in2000.team18.airborn.model.degrees
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.hpa
import no.uio.ifi.in2000.team18.airborn.model.knots
import no.uio.ifi.in2000.team18.airborn.model.m

data class Metar(
    val icao: Icao,
    val time: MetarDateTime,
    val wind: Pair<MetarWind, Pair<Int, Int>?>,
    val cav: Cav,
    val temperatures: Pair<Temperature, Temperature>,
    val altimeterSetting: Pressure,
    val rest: String,
)

data class MetarDateTime(val day: Int, val hour: Int, val minute: Int) {
    override fun toString(): String = "$day. $hour:$minute"
}

sealed interface Visibility {
    data object NoVisibility : Visibility // Less than 50 m
    data object Clear : Visibility // More than 10 km
    data class Distance(val distance: Int) : Visibility // Distance specified
}

sealed interface Cav {
    data object OK : Cav
    data class Info(
        val visibility: Pair<Visibility, List<Pair<Visibility, String>>>,
        val rvrs: List<Rvr>,
        val weatherPhenomena: List<WeatherPhenomenon>,
        val clouds: Clouds
    ) : Cav
}

data class WeatherPhenomenon(
    val qualifier: String,
    val descriptor: String?,
    val precipitation: List<String>,
    val obscuration: List<String>,
    val other: List<String>
)

sealed interface MetarWindDirection {
    data object Variable : MetarWindDirection
    data class Constant(val direction: Direction) : MetarWindDirection
}

data class MetarWind(
    val direction: MetarWindDirection,
    val speed: Speed,
    val gustSpeed: Speed?,
)

sealed interface RvrVisibility {
    data class MoreThan(val distance: Distance) : RvrVisibility
    data class LessThan(val distance: Distance) : RvrVisibility
    data class Exactly(val distance: Distance) : RvrVisibility
    data class Range(val min: Distance, val max: Distance) : RvrVisibility
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
    FEW, SCT, BKN, VV, OVC, Unknown,
}

enum class CloudType {
    Cumulus, // "CU"
    ToweringCumulus, // "TCU"
    Unknown, // "///"
    Nothing, // "";
    Cumulonimbus, // CB
}

data class CloudLayer(
    val cover: SkyCover,
    val height: Int,
    val type: CloudType,
)

sealed interface Clouds {
    data class Layers(val layers: List<CloudLayer>) : Clouds
    data object NCD : Clouds
    data object NSC : Clouds
}


private val metarParser = Unit.let {
    val metarHeader = Unit.let {
        val icao = chars { it.isLetter() }.map { Icao(it) }
        val metarTime = lift(twoDigitNumber, twoDigitNumber, twoDigitNumber) { day, hour, minute ->
            MetarDateTime(day, hour, minute)
        }.skip(char { it.lowercaseChar() == 'z' })
        triple(
            word("METAR").or(word("SPECI")).or(word("AUTO")).skipSpace().optional("METAR"),
            icao.skipSpace(),
            metarTime,
        )
    }
    val windInfo = Unit.let {
        val wind =
            triple(threeDigitNumber.map<MetarWindDirection> { MetarWindDirection.Constant(it.degrees) }
                .or(word("VRB").map { MetarWindDirection.Variable }),
                number,
                word("G").bind { number }.optional()
            ).bind { data ->
                var direction = data.first
                val speed = data.second
                var gustSpeed = data.third
                // TODO: does K mean something different than KT
                word("KT").or(word("K")).map {
                    MetarWind(direction, speed.let { it.knots }, gustSpeed?.let { it.knots })
                }
            }
        val variableWinds = pair(threeDigitNumber.skip(char("V")), threeDigitNumber).optional()
        pair(wind.skipSpace(), variableWinds)
    }
    val cav = Unit.let {
        val cavok: Parser<Cav> = word("CAVOK").or(word("CAVOC")).map { Cav.OK }
        val visibility = Unit.let {
            val cardinalDirectionLetter = either(
                word("NE").map { "North East" },
                word("SE").map { "South East" },
                word("NW").map { "North West" },
                word("SW").map { "South West" },
                char('N').map { "North" },
                char('E').map { "East" },
                char('S').map { "South" },
                char('W').map { "West" },
            )
            val clear =
                word("9999").or(word("10SM")).map<Visibility> { Visibility.Clear }.skip(word("NDV"))
            val noVisibility: Parser<Visibility> = word("0000").map { Visibility.NoVisibility }
            val distance: Parser<Visibility> = number.map { Visibility.Distance(it) }
            val visibility =
                clear.or(noVisibility).or(distance).bind { v -> word("NDV").optional().map { v } }
            val directionalVisibility = pair(visibility.skipSpace(), cardinalDirectionLetter)
            pair(visibility.skipSpace(), many(directionalVisibility).optional(listOf()))
        }

        val rvr = Unit.let {
            val runway = lift(
                char('R'), number, char('R').or(char('L')).or(char('C')).optional()
            ) { _, number, p -> "$number${p ?: ""}" }
            val visibilityPart = either(
                pair(char('M').or(char('P')).optional(), number).map {
                    when (it.first) {
                        'M' -> RvrVisibility.LessThan(it.second.m)
                        'P' -> RvrVisibility.MoreThan(it.second.m)
                        null -> RvrVisibility.Exactly(it.second.m)
                        else -> throw IllegalStateException("This shouldn't happen")
                    }
                },
                pair(number.skip(char('V')), number).map {
                    RvrVisibility.Range(it.first.m, it.second.m)
                },
            )
            val trend = either(
                char('N').map { RvrTrend.NoChange },
                char('U').map { RvrTrend.Upward },
                char('D').map { RvrTrend.Downward },
            ).optional()
            lift(runway.skip(char('/')), visibilityPart, trend) { r, v, t -> Rvr(r, v, t) }
        }

        val weatherPhenomenon = Unit.let {
            val qualifier = either(
                word("-").map { "Light" },
                word("+").map { "Heavy" },
                word("VC").map { "Vicinity" },
                pure("Moderate"), // This has to be last
            )

            val descriptor = either(
                word("BC").map { "Patches" },
                word("BL").map { "Blowing" },
                word("DR").map { "Low Drifting" },
                word("FZ").map { "Freezing" },
                word("MI").map { "Shallow" },
                word("PR").map { "Partial" },
                word("SH").map { "Shower(s)" },
                word("TS").map { "Thunderstorm" },
            )

            val precipitation = either(
                word("DZ").map { "Drizzle" },
                word("GR").map { "Hail" },
                word("GS").map { "Small hail and/or snow pellets" },
                word("IC").map { "Ice crystals" },
                word("PL").map { "Ice pellets" },
                word("RA").map { "Rain" },
                word("SG").map { "Snow Grains" },
                word("SN").map { "Snow" },
                word("UP").map { "Unknown Precipitation" },
            )

            val obsucration = either(
                word("BR").map { "Mist" },
                word("DU").map { "Widespread dust" },
                word("FG").map { "Fog" },
                word("FU").map { "Smoke" },
                word("HZ").map { "Haze" },
                word("SA").map { "Sand" },
                word("VA").map { "Volcanic Ash" },
            )

            val other = either(
                word("DS").map { "Duststorm" },
                word("FC").map { "Funnel Clouds" },
                word("PO").map { "Dust/Sand Whirls" },
                word("SQ").map { "Squall(s)" },
                word("SS").map { "Sandstorm" },
            )

            lift(
                qualifier,
                descriptor.optional(),
                many(precipitation).optional(listOf()),
                many(obsucration).optional(listOf()),
                many(other).optional(listOf()),
            ) { qualifier, descriptor, precipitation, obscuration, other ->
                WeatherPhenomenon(qualifier, descriptor, precipitation, obscuration, other)
            }.filter({
                it.qualifier != "Moderate" //
                        || it.descriptor != null //
                        || it.precipitation.isNotEmpty() //
                        || it.obscuration.isNotEmpty()  //
                        || it.other.isNotEmpty()
            }, expected = "A weather phenomenon")
        }

        val cloudLayer = Unit.let {
            val skyCover = either(
                word("FEW").map { SkyCover.FEW }, // TODO: Is this always a three digit number
                word("SCT").map { SkyCover.SCT },
                word("BKN").map { SkyCover.BKN },
                word("VV").map { SkyCover.VV },
                word("OVC").map { SkyCover.OVC },
                word("///").map { SkyCover.Unknown },
            )

            val cloudType = either(
                word("CB").map { CloudType.Cumulonimbus },
                word("CU").map { CloudType.Cumulus },
                word("TCU").map { CloudType.ToweringCumulus },
                word("///").map { CloudType.Unknown },
                pure(CloudType.Nothing),
            )

            lift(skyCover, number, cloudType) { cover, number, cloudType ->
                CloudLayer(cover, number, cloudType)
            }
        }
        val clouds = either(
            word("NCD").map { Clouds.NCD },
            word("NSC").map { Clouds.NSC },
            many(cloudLayer.skipSpace()).optional(listOf()).map { Clouds.Layers(it) },
        )

        cavok.or(lift(
            visibility.skipSpace(), many(rvr.skipSpace()),
            many(weatherPhenomenon.skipSpace()).optional(listOf()), clouds,
        ) { visibility, rvrs, weatherPhenomena, clouds ->
            Cav.Info(
                visibility, rvrs, weatherPhenomena, clouds
            )
        })
    }
    val temperatures = Unit.let {
        val temperature =
            char('M').optional().bind { m -> number.map { if (m == null) it else -it } }
                .map { it.celsius }
        pair(temperature.skip(char('/')), temperature)
    }
    val altimeterSetting = Unit.let {
        char('Q').bind { number.map { it.hpa } }
    }

    lift(
        metarHeader.skipSpace(),
        windInfo.skipSpace(),
        cav.skipSpace(),
        temperatures.skipSpace(),
        altimeterSetting.skipSpace(),
        chars { it != '=' },
    ) { header, wind, cav, temperatures, altimeterSetting, rest ->
        Metar(header.second, header.third, wind, cav, temperatures, altimeterSetting, rest)
    }.skip(char('='))
}

fun parseMetar(source: String): ParseResult<Metar> = metarParser.parse(source)