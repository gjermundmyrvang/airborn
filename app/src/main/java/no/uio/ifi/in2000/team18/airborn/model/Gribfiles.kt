package no.uio.ifi.in2000.team18.airborn.model

typealias GribFiles = ArrayList<GribFile>

data class GribFile(
    val endpoint: String,
    val params: GribFileParams,
    val updated: String,
    val uri: String
)

data class GribFileParams(
    val area: String,
    val time: String
)
