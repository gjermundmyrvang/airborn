package no.uio.ifi.in2000.team18.airborn.model

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import java.time.ZonedDateTime

data class RouteIsobaric(
    val departure: Airport,
    val arrival: Airport,
    val timeSeries: Map<ZonedDateTime, List<GribFile>>,
    val isobaric: Map<Position, Map<ZonedDateTime, List<IsobaricData?>>>,
    val distance: Distance,
    val bearing: Direction,
)
