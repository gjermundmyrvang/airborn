package no.uio.ifi.in2000.team18.airborn.model.isobaric

import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Speed

data class IsobaricLayer(
    val pressure: Double,
    val temperature: Double,
    val uWind: Double,
    val vWind: Double,
    var windFromDirection: Direction? = null,
    var windSpeed: Speed? = null,
    var height: Double? = null
)
