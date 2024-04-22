package no.uio.ifi.in2000.team18.airborn.model.isobaric

import no.uio.ifi.in2000.team18.airborn.model.Position
import java.time.LocalDateTime

data class IsobaricData(
    val position: Position,
    val time: LocalDateTime,
    val data: List<IsobaricLayer>
)
