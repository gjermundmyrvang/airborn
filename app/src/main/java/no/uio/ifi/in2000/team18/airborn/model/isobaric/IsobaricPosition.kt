package no.uio.ifi.in2000.team18.airborn.model.isobaric

import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Position
import java.time.ZonedDateTime

data class IsobaricPosition(
    val position: Position,
    val fraction: Double,
    val timeSeries: Map<ZonedDateTime, List<IsobaricData>>? = null,
    val distance: Distance? = null,
    val bearing: Direction? = null,
)