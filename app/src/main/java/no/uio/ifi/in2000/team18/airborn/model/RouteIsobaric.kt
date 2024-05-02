package no.uio.ifi.in2000.team18.airborn.model

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.RouteProgress
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import java.time.ZonedDateTime

data class RouteIsobaric(
    val departure: Airport,
    val arrival: Airport,
    val isobaric: IsobaricData,
    val distance: Distance,
    val bearing: Direction,
    val currentPos: Position,
    var positions: Map<RouteProgress, Position>? = null,
    var timeSeries: Map<ZonedDateTime, GribFile>? = null,
)


