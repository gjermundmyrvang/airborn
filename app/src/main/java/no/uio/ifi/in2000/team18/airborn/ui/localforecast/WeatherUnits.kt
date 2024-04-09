package no.uio.ifi.in2000.team18.airborn.ui.localforecast

data class Hpa(val value: Double) {
    override fun toString(): String = "$value hPa"
}

data class Humidity(val value: Double) {
    override fun toString(): String = "$value %"
}

data class MetersPerSecond(val value: Double) {
    override fun toString(): String = "$value m/s"
}

data class Celsius(val value: Double) {
    override fun toString(): String = "$value \u2103"
}

data class DirectionInDegrees(val value: Double) {
    override fun toString(): String = "$value degrees"
}

data class CloudFraction(
    val cloudFraction: Double,
    val cloudFractionHigh: Double,
    val cloudFractionMedium: Double,
    val cloudFractionLow: Double,
) {
    override fun toString(): String {
        return "Cloud fraction: $cloudFraction%\nCloud fraction high: $cloudFractionHigh%\nCloud fraction medium: $cloudFractionLow%\nCloud fraction low: $cloudFractionLow%"
    }
}

data class FogAreaFraction(val value: Double) {
    override fun toString(): String = "$value %"
}

data class UvIndex(val value: Double) {
    override fun toString(): String = "$value"
}

fun main() {
    println(Celsius(20.9))
}