package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import no.uio.ifi.in2000.team18.airborn.ui.common.ZDTAdapter
import java.time.ZonedDateTime

typealias GribFiles = ArrayList<GribFile>

data class GribFile(
    val endpoint: String,
    val params: GribFileParams,
    val updated: String,
    val uri: String
)

data class GribFileParams(
    val area: String,
    @JsonAdapter(ZDTAdapter::class) val time: ZonedDateTime
)


