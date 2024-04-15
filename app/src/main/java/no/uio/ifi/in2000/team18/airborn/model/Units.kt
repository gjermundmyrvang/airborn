package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlin.math.atan2
import kotlin.math.round

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
    override fun toString(): String = "%.0f kn".format(knots)
    val kmh get() = this.mps * 3.6
    val knots get() = this.mps * 1.9438452
    operator fun times(x: Number) = Speed(mps = mps * x.toDouble())
    operator fun plus(x: Speed) = Speed(mps + x.mps)
}

data class Temperature(val kelvin: Double) {
    override fun toString(): String = "%.0f\u2103".format(celsius)

    val celsius get() = this.kelvin - 273.15
}

data class Direction(var degrees: Double) {

    init {
        degrees = Math.floorMod(this.degrees.toInt(), 360).toDouble()
    }

    override fun toString(): String = "%.0f\u00B0".format(degrees)

    companion object {
        val EAST: Direction = 90.degrees
        val WEST: Direction = 270.degrees
        val NORTH: Direction = 0.degrees
        val SOUTH: Direction = 180.degrees

        fun fromWindUV(u: Double, v: Double) = atan2(-u, -v).radians
    }

    // TODO: is this unnecessary? Or at least use new property after added init...
    override fun equals(other: Any?) =
        when (other) {
            is Direction ->
                Math.floorMod(this.degrees.toInt(), 360) == Math.floorMod(
                    other.degrees.toInt(),
                    360
                )

            else -> false
        }
}

data class UvIndex(val uv: Double) {
    override fun toString(): String = "$uv"
}

data class Distance(val meters: Double) {
    override fun toString(): String = if (meters < 1) "${meters * 1000} mm"
    else if (meters < 1000) "${meters} m"
    else "${meters / 1000} km"

    fun toStringAsFeet(): String = "${(round(feet / 10) * 10).toInt()} ft"
    val feet get() = meters * 3.2808399
    val nauticalMiles get() = meters * 0.000539956803

    operator fun times(x: Number) = Distance(meters = meters * x.toDouble())
    operator fun plus(x: Distance) = Distance(meters + x.meters)
}

data class Fraction(val fraction: Double) {
    override fun toString(): String = "$fraction %"
}


// Speed
private operator fun Number.times(s: Speed) = s * this
val Number.mps get() = Speed(mps = this.toDouble())
val Number.kmph get() = this * (1 / 3.6).mps
val Number.knots get() = this * 0.51444424416.mps

// Temperature
val Number.celsius get() = Temperature(kelvin = this.toDouble() + 273.15)


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
