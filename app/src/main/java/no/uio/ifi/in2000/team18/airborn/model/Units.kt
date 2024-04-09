package no.uio.ifi.in2000.team18.airborn.model

data class Hpa(val value: Double) {
    override fun toString(): String = "$value hPa"
}

data class Humidity(val value: Double) {
    override fun toString(): String = "$value %"
}

data class Speed(val mps: Double) {
    override fun toString(): String = "$mps m/s"
    val kmh get() = this.mps * 3.6
    val knots get() = this.mps * 1.9438452
}

data class Temperature(val celcius: Double) {
    override fun toString(): String = "$celcius \u2103"
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

val Double.mps get() = Speed(mps = this)
val Int.mps get() = this.toDouble().mps

val Double.kmph get() = Speed(mps = this / 3.6)
val Int.kmph get() = this.toDouble().kmph

val Double.knots get() = Speed(mps = 0.51444424416 * this)
val Int.knots get() = this.toDouble().knots

val Double.celcius get() = Temperature(celcius = this)
val Int.celcius get() = this.toDouble().knots
