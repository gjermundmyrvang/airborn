package no.uio.ifi.in2000.team18.airborn.model.isobaric

import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Pressure
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.Temperature

data class IsobaricLayer(
    val pressure: Pressure,
    val temperature: Temperature,
    val uWind: Double,
    val vWind: Double,
    var windFromDirection: Direction? = null,
    var windSpeed: Speed? = null,
    var height: Distance? = null
)
