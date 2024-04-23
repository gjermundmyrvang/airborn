package no.uio.ifi.in2000.team18.airborn.model

import alexmaryin.metarkt.parser.formatToFloat
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class CelsiusAdapter : TypeAdapter<Temperature>() {
    override fun write(writer: JsonWriter, value: Temperature) = writer.value(value.celsius).let {}
    override fun read(reader: JsonReader): Temperature = Temperature(reader.nextDouble())
}

class MpsAdapter : TypeAdapter<Speed>() {
    override fun write(writer: JsonWriter, value: Speed) = writer.value(value.mps).let {}
    override fun read(reader: JsonReader): Speed = reader.nextDouble().mps
}

class DirectionAdapter : TypeAdapter<Direction>() {
    override fun write(writer: JsonWriter, value: Direction) = writer.value(value.degrees).let {}
    override fun read(reader: JsonReader): Direction = reader.nextDouble().degrees
}

class UvAdapter : TypeAdapter<UvIndex>() {
    override fun write(writer: JsonWriter, value: UvIndex) = writer.value(value.uv).let {}
    override fun read(reader: JsonReader): UvIndex = reader.nextDouble().uv
}

class PressureAdapter : TypeAdapter<Pressure>() {
    override fun write(writer: JsonWriter, value: Pressure) = writer.value(value.hpa).let {}
    override fun read(reader: JsonReader): Pressure = reader.nextDouble().hpa
}

class HumidityAdapter : TypeAdapter<Humidity>() {
    override fun write(writer: JsonWriter, value: Humidity) = writer.value(value.humidity).let {}
    override fun read(reader: JsonReader): Humidity = reader.nextDouble().humidity
}

class FractionAdapter : TypeAdapter<Fraction>() {
    override fun write(writer: JsonWriter, value: Fraction) = writer.value(value.fraction).let {}
    override fun read(reader: JsonReader): Fraction = reader.nextDouble().fraction
}


data class Pressure(val hpa: Double) {
    override fun toString(): String = "$hpa hPa"
    operator fun times(x: Number) = Pressure(hpa = hpa * x.toDouble())
    operator fun plus(x: Pressure) = Pressure(hpa + x.hpa)
    fun toDouble(): Double = hpa
}

data class Humidity(val humidity: Double) {
    override fun toString(): String = "$humidity %"
}

data class Speed(val mps: Double) {
    override fun toString(): String = "$mps m/s"
    fun formatAsKnots(decimals: Int = 0): String = "${knots.format(decimals)} kt"
    fun formatAsMps(decimals: Int = 0): String = "${mps.format(decimals)} m/s"
    val kmh get() = this.mps * 3.6
    val knots get() = this.mps * 1.9438452
    operator fun times(x: Number) = Speed(mps = mps * x.toDouble())
    operator fun plus(x: Speed) = Speed(mps + x.mps)
}

data class Temperature(val celsius: Double) {
    override fun toString(): String = "${celsius.roundToInt()} \u2103"
    val kelvin get() = this.celsius + 273.15
}

data class Direction(var degrees: Double) {

    init {
        degrees = Math.floorMod(this.degrees.toInt(), 360).toDouble()
    }

    override fun toString(): String = "${degrees.roundToInt()}\u00B0"

    companion object {
        val EAST: Direction = 90.degrees
        val WEST: Direction = 270.degrees
        val NORTH: Direction = 0.degrees
        val SOUTH: Direction = 180.degrees

        fun fromWindUV(u: Double, v: Double) = atan2(-u, -v).radians
    }

    override fun equals(other: Any?) = when (other) {
        is Direction -> this.degrees.toInt() == other.degrees.toInt()
        else -> false
    }

    fun formatAsPrincipal() = listOf(
        "north",
        "northeast",
        "east",
        "southeast",
        "south",
        "southwest",
        "west",
        "northwest",
    )[Math.floorMod((degrees + 22.5).toInt().floorDiv(45), 8)]

    fun formatAsDegrees(decimals: Int = 0) = "${degrees.format(decimals)}\u00B0"
}

data class UvIndex(val uv: Double) {
    override fun toString(): String = "$uv"
}

data class Distance(val meters: Double) {
    override fun toString(): String = if (meters < 1) "${meters * 1000} mm"
    else if (meters < 1000) "${meters} m"
    else "${meters / 1000} km"

    fun formatAsFeet(): String = "${(round(feet / 10) * 10).toInt()} ft"
    fun formatAsNm(): String = "${nauticalMiles.roundToInt()} nm"
    val feet get() = meters * 3.2808399
    val nauticalMiles get() = meters * 0.000539956803

    operator fun times(x: Number) = Distance(meters = meters * x.toDouble())
    operator fun plus(x: Distance) = Distance(meters + x.meters)
}

data class Fraction(val fraction: Double) {
    override fun toString(): String = "$fraction %"
}

data class Position(
    val latitude: Double, val longitude: Double
) {
    override fun toString(): String = "($latitude, $longitude)"

    companion object {
        const val EARTH_RADIUS_METERS: Double = 6371000.0
    }

    fun toPoints(): Point = Point.fromLngLat(longitude, latitude)

    /**
     * Giving great-circle distances to another position on a sphere.
     * Using the haversine formula.
     *
     * https://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param destination Position of destination
     * @return Distance to destination
     */
    fun distanceTo(destination: Position): Distance {
        val startLat = Math.toRadians(this.latitude)
        val endLat = Math.toRadians(destination.latitude)
        val deltaLat = Math.toRadians(destination.latitude - this.latitude)
        val deltaLon = Math.toRadians(destination.longitude - this.longitude)

        val a = sin(deltaLat / 2).pow(2) +
                cos(startLat) * cos(endLat) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return Distance(EARTH_RADIUS_METERS * c)
    }

    /**
     * Halfway to another position along a great-circle arc
     * (along the surface-path on the globe).
     * https://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param destination Position of interest
     * @return Position of midpoint, halfway to destination.
     */
    fun halfwayTo(destination: Position): Position {
        val startLon = Math.toRadians(this.longitude)
        val startLat = Math.toRadians(this.latitude)
        val endLon = Math.toRadians(destination.longitude)
        val endLat = Math.toRadians(destination.latitude)
        val deltaLon = endLon - startLon

        val x = cos(endLat) * cos(deltaLon)
        val y = cos(endLat) * sin(deltaLon)
        val lat = atan2(
            sin(startLat) + sin(endLat),
            sqrt((cos(startLat) + x) * (cos(startLat) + x) + y * y)
        )

        val lon = startLon + atan2(y, cos(startLat) + x)
        return Position(Math.toDegrees(lat), Math.toDegrees(lon))
    }

    /**
     * Follow a great-circle line between two positions.
     * https://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param destination Position of destination
     * @return Direction of initial bearing to destination
     */
    fun bearingTo(destination: Position): Double {
        val startLon = Math.toRadians(this.longitude)
        val startLat = Math.toRadians(this.latitude)
        val endLon = Math.toRadians(destination.longitude)
        val endLat = Math.toRadians(destination.latitude)
        val deltaLon = endLon - startLon

        val y = sin(deltaLon) * cos(endLat)
        val x = (cos(startLat) * sin(endLat)).minus(sin(startLat) * cos(endLat) * cos(deltaLon))
        val theta = atan2(y, x)
        return Math.toDegrees((theta)).mod(360.0) // bearing in degrees
    }
}

// Speed
private operator fun Number.times(s: Speed) = s * this
val Number.mps get() = Speed(mps = this.toDouble())
val Number.kmph get() = this * (1 / 3.6).mps
val Number.knots get() = this * 0.51444424416.mps

// Temperature
val Number.kelvin get() = Temperature(celsius = this.toDouble() - 273.15)
val Number.celsius get() = Temperature(celsius = this.toDouble())

// Pressure
private operator fun Number.times(s: Pressure) = s * this
val Number.hpa get() = Pressure(hpa = this.toDouble())
val Number.pa get() = this * 0.1.hpa


// Directions
val Number.degrees get() = Direction(degrees = this.toDouble())
val Number.radians get() = (this.toDouble() / Math.PI * 180).degrees


val Number.uv get() = UvIndex(uv = this.toDouble())
val Number.humidity get() = Humidity(humidity = this.toDouble())
val Number.fraction get() = Fraction(fraction = this.toDouble())

// Distance
private operator fun Number.times(m: Distance) = m * this
val Number.m get() = Distance(meters = this.toDouble())
val Number.km get() = this * 1000.m
val Number.mm get() = this * 0.001.m
val Number.feet get() = this * 0.3048.m
val Number.nauticalMiles get() = this * 1852.m

// Utilities:
fun Double.format(decimals: Int) =
    if (decimals <= 0) "${this.round(decimals).roundToInt()}"
    else "${this.round(decimals).formatToFloat(decimals)}"

fun Double.round(decimals: Int): Double {
    var multiplier = Math.pow(10.0, decimals.toDouble())
    return round(this * multiplier) / multiplier
}

