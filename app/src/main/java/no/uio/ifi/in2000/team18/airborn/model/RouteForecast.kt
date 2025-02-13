package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import java.time.ZonedDateTime

data class RouteForecast(
    val params: RouteParams, val uri: String
)

data class RouteParams(
    val route: String, @JsonAdapter(ZonedDateTimeAdapter::class) val time: ZonedDateTime
)