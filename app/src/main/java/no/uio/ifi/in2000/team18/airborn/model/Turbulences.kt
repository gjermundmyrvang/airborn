package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import java.time.ZonedDateTime

data class Turbulence(
    val params: Params,
    val uri: String
)

data class Params(
    val icao: String,
    @JsonAdapter(ZonedDateTimeAdapter::class) val time: ZonedDateTime,
    val type: String
)

