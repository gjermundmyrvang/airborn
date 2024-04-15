package no.uio.ifi.in2000.team18.airborn.model

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position

data class SigmetDateTime(val day: Int, val hour: Int, val minute: Int) {
    override fun toString(): String = "$day. $hour:$minute"
}


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
    val dateTime: SigmetDateTime,
    val regionCode: String,
    val type: SigmetType,
    val identifier: Pair<Char, Int>,
    val timeRange: Pair<SigmetDateTime, SigmetDateTime>,
    val location: String,
    val extra: String?,
    val message: List<String>,
    val coordinates: List<Position>,
    val altitude: Pair<AltitudeReference, AltitudeReference>?,
)
