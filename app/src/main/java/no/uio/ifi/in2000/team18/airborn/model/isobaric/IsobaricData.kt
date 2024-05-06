package no.uio.ifi.in2000.team18.airborn.model.isobaric

import no.uio.ifi.in2000.team18.airborn.model.Position
import java.time.ZonedDateTime

data class IsobaricData(
    val position: Position,
    val time: ZonedDateTime,
    val data: List<IsobaricLayer>,
    val timeSeries: List<ZonedDateTime>
)
