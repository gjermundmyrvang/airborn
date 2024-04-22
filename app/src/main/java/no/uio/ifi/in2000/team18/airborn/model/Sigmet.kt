package no.uio.ifi.in2000.team18.airborn.model


import no.uio.ifi.in2000.team18.airborn.model.flightbrief.ParsedDateTime
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position


enum class SigmetType {
    Airmet,
    Sigmet,
}


enum class AltitudeReferenceType {
    Feet, FlightLevel, Unknown,
}

data class AltitudeReference(
    val typ: AltitudeReferenceType,
    val number: Int,
)

data class Sigmet(
    val issuingAuthority: String,
    val originatingLocation: String,
    val dateTime: ParsedDateTime,
    val regionCode: String,
    val type: SigmetType,
    val identifier: Pair<Char, Int>,
    val timeRange: Pair<ParsedDateTime, ParsedDateTime>,
    val location: String,
    val extra: String?,
    val message: List<String>,
    val coordinates: List<Position>,
    val altitude: Pair<AltitudeReference, AltitudeReference>?,
)
