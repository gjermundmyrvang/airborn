package no.uio.ifi.in2000.team18.airborn.model

data class Turbulence(
    val params: Params,
    val uri: String
)

data class Params(
    val icao: String,
    val time: String,
    val type: Type
)

enum class Type {
    map,
    cross_section
}
