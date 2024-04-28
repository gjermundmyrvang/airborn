package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTimeAdapter

data class Turbulence(
    val params: Params,
    val uri: String
)

data class Params(
    val icao: String,
    @JsonAdapter(DateTimeAdapter::class) val time: DateTime,
    val type: String
)

