package no.uio.ifi.in2000.team18.airborn.data.repository.parsers

import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team18.airborn.model.AltitudeReference
import no.uio.ifi.in2000.team18.airborn.model.AltitudeReferenceType
import no.uio.ifi.in2000.team18.airborn.model.ParsedDateTime
import no.uio.ifi.in2000.team18.airborn.model.Sigmet
import no.uio.ifi.in2000.team18.airborn.model.SigmetType


private val metParser = Unit.let {
    data class SigmetHeader(
        val issuingAuthority: String, val location: String, val dateTime: ParsedDateTime
    )

    data class SigmetSigmeta(
        val regionCode: String,
        val type: SigmetType,
        val identifier: Pair<Char, Int>,
        val timeRange: Pair<ParsedDateTime, ParsedDateTime>,
        val location: String,
        val extra: String?,
    )

    data class SigmetBody(
        val message: List<String>,
        val coordinates: List<Point>,
        val altitude: Pair<AltitudeReference, AltitudeReference>?,
    )


    // Sigmet stuff
    val metTime = lift(twoDigitNumber, twoDigitNumber, twoDigitNumber) { day, hour, minute ->
        ParsedDateTime(day, hour, minute)
    }

    val issuingAuthority = chars1("an authority code") { it.isLetter() or it.isDigit() }
    val locationIndicator = chars1("a location indicator") { it.isLetter() }
    val metHeader = lift(
        issuingAuthority.skipSpace(), locationIndicator.skipSpace(), metTime
    ) { authority, location, time ->
        SigmetHeader(authority, location, time)
    }

    val regionCode = chars1("a region code") { it.isLetter() }
    val metType =
        word("AIRMET").map { SigmetType.Airmet }.or(word("SIGMET").map { SigmetType.Sigmet })
            .skipSpace()
    val metIdentifier = pair(char { it.isLetter() }, twoDigitNumber)
    val metSigmeta = lift(
        regionCode.skipSpace(),
        metType.skipSpace(),
        metIdentifier.skipSpace().skip(word("VALID")).skipSpace(),
        pair(metTime.skip(word("/")), metTime).skipSpace(),
        locationIndicator.skip(word("-")),
        chars1("") { it != '\n' }.optional()
    ) { region, t, i, tr, loc, ex ->
        SigmetSigmeta(
            region, t, i, tr, loc, ex
        )
    }

    val flightInformationRegion = lift(
        word("ENOR POLARIS").or(word("ENOB BODOE OCEANIC")).skipSpace(),
        word("FIR"),
    ) { _, _ ->
    }

    val lat = lift(
        word("N").or(word("S")),
        twoDigitNumber,
        chars { it.isDigit() }) { dir, whole, decimal ->
        (if (dir == "N") 1 else -1) * ("$whole.$decimal").toDouble()
    }
    val lon = lift(
        word("E").or(word("W")),
        threeDigitNumber,
        chars { it.isDigit() }) { dir, whole, decimal ->
        (if (dir == "E") 1 else -1) * ("$whole.$decimal").toDouble()
    }
    val coordinate = lift(lat.skipSpace(), lon) { a, b -> Point.fromLngLat(b, a) }
    val coordinateList = sepBy(
        coordinate.skipSpace(), word("-").skipSpace()
    )

    val flAltitudeReference =
        lift(word("FL"), number) { _, n -> AltitudeReference(AltitudeReferenceType.FlightLevel, n) }
    val ftAltitudeReference =
        lift(number, word("FT")) { n, _ -> AltitudeReference(AltitudeReferenceType.Feet, n) }
    val numberAltitudeReference =
        number.map { AltitudeReference(AltitudeReferenceType.Unknown, it) }
    val altitudeReference = ftAltitudeReference.or(flAltitudeReference).or(numberAltitudeReference)
    val alititudeRange = lift(altitudeReference.skip(word("/")), altitudeReference) { r1, r2 ->
        Pair(r1, if (r2.typ == AltitudeReferenceType.Unknown) r2.copy(typ = r1.typ) else r2)
    }


    val messagePart = chars1("a word") { it.isLetter() }.skip(word(" ")).skipSpace()
    val message = many1(messagePart)

    val metBody = lift(
        flightInformationRegion.skipSpace(),
        message,
        coordinateList.skipSpace(),
        alititudeRange.optional(),
    ) { _, m, c, r ->
        SigmetBody(
            message = m,
            coordinates = c,
            altitude = r,
        )
    }


    val met = pure(Unit).skipSpace().skip(word("ZCZC")).skip(word("\n")).bind {
        lift(
            metHeader.skip(word("\n")),
            metSigmeta.skip(word("\n")),
            metBody,
        ) { header, meta, body ->
            Sigmet(
                header.issuingAuthority,
                header.location,
                header.dateTime,
                meta.regionCode,
                meta.type,
                meta.identifier,
                meta.timeRange,
                meta.location,
                meta.extra,
                body.message,
                body.coordinates,
                body.altitude,
            )
        }
    }.skip(
        chars { it != '=' }
    ).skip(char('=').optional())


    met
}

val metsParser = many1(
    metParser.skipSpace()
)

fun parseSigmet(source: String): ParseResult<Sigmet> = metParser.parse(source)

fun parseSigmets(source: String): List<Sigmet> = when (val result = metsParser.parse(source)) {
    is ParseResult.Ok -> result.value
    is ParseResult.Error -> listOf()
}