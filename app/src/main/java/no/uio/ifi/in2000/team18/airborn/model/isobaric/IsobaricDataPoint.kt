package no.uio.ifi.in2000.team18.airborn.model.isobaric

import java.time.ZonedDateTime

data class IsobaricDataPoint(
    val isobaricDataTimeSeries: Map<ZonedDateTime, List<IsobaricData>>? = null
)