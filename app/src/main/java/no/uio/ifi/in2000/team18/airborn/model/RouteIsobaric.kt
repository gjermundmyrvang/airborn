package no.uio.ifi.in2000.team18.airborn.model

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData

data class RouteIsobaric(
    val departure: Airport,
    val arrival: Airport,
    val isobaric: IsobaricData,
    val distance: Distance,
    val bearing: Double,
)
