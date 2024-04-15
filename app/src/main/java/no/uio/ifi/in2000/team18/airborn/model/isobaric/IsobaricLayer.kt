package no.uio.ifi.in2000.team18.airborn.model.isobaric

data class IsobaricLayer(
    val pressure: Double,
    val temperature: Double,
    val uWind: Double,
    val vWind: Double,
    var windFromDirection: Double? = null,
    var windSpeed: Double? = null,
    var height: Double? = null
)
