package no.uio.ifi.in2000.team18.airborn.model

data class UVResponse(
    val result: UVResult
)

data class UVResult(
    val uv: Double,
    val uvTime: String,
    val uvMax: Double,
    val uvMaxTime: String,
    val ozone: Double,
    val ozoneTime: String,
    val safeExposureTime: Map<String, Long>,
    val sunInfo: SunInfo
)

data class SunInfo(
    val sunTimes: SunTimes,
    val sunPosition: SunPosition
)

data class SunPosition(
    val azimuth: Double,
    val altitude: Double
)

data class SunTimes(
    val solarNoon: String,
    val nadir: String,
    val sunrise: String,
    val sunset: String,
    val sunriseEnd: String,
    val sunsetStart: String,
    val dawn: String,
    val dusk: String,
    val nauticalDawn: String,
    val nauticalDusk: String,
    val nightEnd: String,
    val night: String,
    val goldenHourEnd: String,
    val goldenHour: String
)