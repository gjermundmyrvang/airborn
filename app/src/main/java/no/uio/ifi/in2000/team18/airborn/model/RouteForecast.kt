package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTimeAdapter

data class RouteForecast(
    val params: RouteParams, val uri: String
)

data class RouteParams(
    val route: String, @JsonAdapter(DateTimeAdapter::class) val time: DateTime
)