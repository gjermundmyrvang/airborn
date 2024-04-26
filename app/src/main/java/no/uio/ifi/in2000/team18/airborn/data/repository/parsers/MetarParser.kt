package no.uio.ifi.in2000.team18.airborn.data.repository.parsers

import kotlinx.datetime.Instant
import no.uio.ifi.in2000.team18.airborn.model.celsius
import no.uio.ifi.in2000.team18.airborn.model.degrees
import no.uio.ifi.in2000.team18.airborn.model.feet
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Cav
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.CloudLayer
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.CloudType
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Clouds
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarWind
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarWindDirection
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.ParsedDateTime
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Rvr
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.RvrTrend
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.RvrVisibility
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.SkyCover
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.VisibilityDistance
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.WeatherPhenomenon
import no.uio.ifi.in2000.team18.airborn.model.hpa
import no.uio.ifi.in2000.team18.airborn.model.knots
import no.uio.ifi.in2000.team18.airborn.model.m


enum class PhenomenonQualifier {
    Light,
    Heavy,
    Moderate;

    override fun toString() = when (this) {
        Light -> "light"
        Heavy -> "heavy"
        Moderate -> "moderate"
    }
}

enum class PhenomenonDescriptor {
    Patches,
    Blowing,
    LowDrifting,
    Freezing,
    Shallow,
    Partial,
    Shower,
    Thunderstorm;

    override fun toString() = when (this) {
        Patches -> "patches"
        Blowing -> "blowing"
        LowDrifting -> "low drifting"
        Freezing -> "freezing"
        Shallow -> "shallow"
        Partial -> "partial"
        Shower -> "showering"
        Thunderstorm -> "thunderstorm"
    }
}

enum class PhenomenonPrecipitation {
    Drizzle,
    Hail,
    SmallHail, // And/Or snow pellets
    Icecrystals,
    Icepellets,
    Rain,
    SnowGrains,
    Snow,
    UnknownPrecipitation;

    override fun toString() = when (this) {
        Drizzle -> "drizzle"
        Hail -> "hail"
        SmallHail -> "small hail and/or snow pellets"
        Icecrystals -> "ice crystals"
        Icepellets -> "ice pellets"
        Rain -> "rain"
        SnowGrains -> "snow grains"
        Snow -> "snow"
        UnknownPrecipitation -> "unknown"
    }
}

enum class PhenomenonObscuration {
    Mist,
    WidespreadDust,
    Fog,
    Smoke,
    Haze,
    Sand,
    VolcanicAsh;

    override fun toString() = when (this) {
        Mist -> "mist"
        WidespreadDust -> "widespread dust"
        Fog -> "fog"
        Smoke -> "smoke"
        Haze -> "haze"
        Sand -> "sand"
        VolcanicAsh -> "volcanic ash"
    }
}

enum class PhenomenonExtra {
    DustStorm,
    FunnelClouds,
    DustOrSandWhirls,
    Squall,
    Sandstorm;

    override fun toString() = when (this) {
        DustStorm -> "dust storm"
        FunnelClouds -> "funnel clouds"
        DustOrSandWhirls -> "dust/sand whirls"
        Squall -> "squall(s)"
        Sandstorm -> "sandstorm"
    }
}


private val metarParser = Unit.let {
    val metarHeader = Unit.let {
        val icao = chars { it.isLetter() }.map { Icao(it) }
        val metarTime = lift(twoDigitNumber, twoDigitNumber, twoDigitNumber) { day, hour, minute ->
            ParsedDateTime(day, hour, minute)
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
        val direction = threeDigitNumber.map { it.degrees }
        val variableWinds = pair(direction.skip(char("V")), direction).optional()
        pair(wind.skipSpace(), variableWinds)
    }
    val cav = Unit.let {
        val cavok: Parser<Cav> = word("CAVOK").or(word("CAVOC")).map { Cav.OK }
        val visibility = Unit.let {
            val cardinalDirectionLetter = either(
                word("NE").map { 45.degrees },
                word("SE").map { 135.degrees },
                word("NW").map { 315.degrees },
                word("SW").map { 225.degrees },
                char('N').map { 0.degrees },
                char('E').map { 90.degrees },
                char('S').map { 180.degrees },
                char('W').map { 270.degrees },
            )
            val clear =
                word("9999").or(word("10SM")).map<VisibilityDistance> { VisibilityDistance.Clear }
            val noVisibility: Parser<VisibilityDistance> =
                word("0000").map { VisibilityDistance.NoVisibility }
            val distance: Parser<VisibilityDistance> =
                number.map { VisibilityDistance.Distance(it) }
            val visibility = clear.or(noVisibility).or(distance)
            // TODO: Directional visibility
            val directionalVisibility = pair(visibility.skipSpace(), cardinalDirectionLetter)
            pair(visibility.skipSpace(), either(
                word("NDV").map { null },
                pure(Unit).skipSpace().bind { many(directionalVisibility).nullable() }
            ))
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
                word("-").map { PhenomenonQualifier.Light },
                word("+").map { PhenomenonQualifier.Heavy },
                pure(PhenomenonQualifier.Moderate), // This has to be last
            )

            val descriptor = either(
                word("BC").map { PhenomenonDescriptor.Patches },
                word("BL").map { PhenomenonDescriptor.Blowing },
                word("DR").map { PhenomenonDescriptor.LowDrifting },
                word("FZ").map { PhenomenonDescriptor.Freezing },
                word("MI").map { PhenomenonDescriptor.Shallow },
                word("PR").map { PhenomenonDescriptor.Partial },
                word("SH").map { PhenomenonDescriptor.Shower },
                word("TS").map { PhenomenonDescriptor.Thunderstorm },
            )

            val precipitation = either(
                word("DZ").map { PhenomenonPrecipitation.Drizzle },
                word("GR").map { PhenomenonPrecipitation.Hail },
                word("GS").map { PhenomenonPrecipitation.SmallHail },
                word("IC").map { PhenomenonPrecipitation.Icecrystals },
                word("PL").map { PhenomenonPrecipitation.Icepellets },
                word("RA").map { PhenomenonPrecipitation.Rain },
                word("SG").map { PhenomenonPrecipitation.SnowGrains },
                word("SN").map { PhenomenonPrecipitation.Snow },
                word("UP").map { PhenomenonPrecipitation.UnknownPrecipitation },
            )

            val obsucration = either(
                word("BR").map { PhenomenonObscuration.Mist },
                word("DU").map { PhenomenonObscuration.WidespreadDust },
                word("FG").map { PhenomenonObscuration.Fog },
                word("FU").map { PhenomenonObscuration.Smoke },
                word("HZ").map { PhenomenonObscuration.Haze },
                word("SA").map { PhenomenonObscuration.Sand },
                word("VA").map { PhenomenonObscuration.VolcanicAsh },
            )

            val other = either(
                word("DS").map { PhenomenonExtra.DustStorm },
                word("FC").map { PhenomenonExtra.FunnelClouds },
                word("PO").map { PhenomenonExtra.DustOrSandWhirls },
                word("SQ").map { PhenomenonExtra.Squall },
                word("SS").map { PhenomenonExtra.Sandstorm },
            )

            lift(
                qualifier,
                word("VC").optional().map { it != null },
                descriptor.optional(),
                many1(precipitation).optional(listOf()),
                many1(obsucration).optional(listOf()),
                many1(other).optional(listOf()),
            ) { qualifier, inVicinity, descriptor, precipitation, obscuration, other ->
                WeatherPhenomenon(
                    qualifier,
                    descriptor,
                    precipitation,
                    obscuration,
                    other,
                    inVicinity
                )
            }.filter({
                it.qualifier != PhenomenonQualifier.Moderate //
                        || it.descriptor != null //
                        || it.precipitation.isNotEmpty() //
                        || it.obscuration.isNotEmpty()  //
                        || it.other.isNotEmpty()
            }, expected = "A weather phenomenon")
        }

        val cloudLayer = Unit.let {
            val skyCover = either(
                word("FEW").map { SkyCover.FEW },
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

            // TODO: Is this always a three digit number
            lift(skyCover, number, cloudType) { cover, number, cloudType ->
                CloudLayer(cover, (number * 100).feet, cloudType)
            }
        }
        val clouds = either(
            word("NCD").map { Clouds.NCD },
            word("NSC").map { Clouds.NSC },
            many1(cloudLayer.skipSpace()).optional(listOf()).map { Clouds.Layers(it) },
        )

        cavok.or(lift(
            visibility.skipSpace(), many(rvr.skipSpace()),
            many1(weatherPhenomenon.skipSpace()).optional(listOf()), clouds,
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
        Metar.DecodedMetar(
            header.second, header.third,
            wind,
            cav,
            temperatures,
            altimeterSetting,
            rest,
            text = "",
            downloaded = null,
        )
    }.skip(char('='))
}

fun parseMetar(source: String, downloaded: Instant? = null): ParseResult<Metar.DecodedMetar> =
    metarParser.parse(source).map { it.copy(text = source, downloaded = downloaded) }
// ParseResult.Error(listOf())

private fun <T, U> ParseResult<T>.map(f: (T) -> U): ParseResult<U> = when (this) {
    is ParseResult.Ok -> ParseResult.Ok(f(this.value), state)
    is ParseResult.Error -> ParseResult.Error(this.expected)
}