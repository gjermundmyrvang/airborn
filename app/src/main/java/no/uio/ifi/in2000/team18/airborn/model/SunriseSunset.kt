package no.uio.ifi.in2000.team18.airborn.model

data class SunriseSunset(
    val copyright: String,
    val licenseURL: String,
    val type: String,
    val geometry: Geometry,
    val welcomeWhen: When,
    val properties: SunriseSunsetProperties
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

data class SunriseSunsetProperties(
    val body: String,
    val sunrise: Sun,
    val sunset: Sun,
    val solarnoon: Solar,
    val solarmidnight: Solar
)

data class Solar(
    val time: String,
    val discCentreElevation: Double,
    val visible: Boolean
)

data class Sun(
    val time: String,
    val azimuth: Double
)

data class When(
    val interval: List<String>
)

