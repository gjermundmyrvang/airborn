package no.uio.ifi.in2000.team18.airborn.model


data class RadarContent(
    val area: String,
    val content: String,
    val time: String,
    val type: String,
)

data class Radar(
    val params: RadarContent,
    val uri: String
)

