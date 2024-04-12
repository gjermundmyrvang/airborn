package no.uio.ifi.in2000.team18.airborn.model

data class Sigchart(
    val params: SigchartParameters, val updated: String, val uri: String
)

data class SigchartParameters(
    val area: Area, val time: String
)

enum class Area {
    nordic, norway
}