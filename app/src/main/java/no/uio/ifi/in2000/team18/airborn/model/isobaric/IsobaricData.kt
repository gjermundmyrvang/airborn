package no.uio.ifi.in2000.team18.airborn.model.isobaric

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position

data class IsobaricData(
    val position: Position,
    val time: String,
    val data: List<IsobaricLayer>
)
