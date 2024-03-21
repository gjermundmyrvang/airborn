package no.uio.ifi.in2000.team18.airborn.model.isobaric

data class IsobaricLayer(
    val pressure: Double,
    val temperature: Double,
    val windFromDirection: Double? = null,
    val windSpeed: Double? = null,
    var height: Double? = null
)
