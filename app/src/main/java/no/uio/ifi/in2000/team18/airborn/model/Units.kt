package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

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
}

data class Humidity(val humidity: Double) {
    override fun toString(): String = "$humidity %"
}

data class Speed(val mps: Double) {
    override fun toString(): String = "$mps m/s"
    val kmh get() = this.mps * 3.6
    val knots get() = this.mps * 1.9438452
    operator fun times(x: Number) = Speed(mps = mps * x.toDouble())
    operator fun plus(x: Speed) = Speed(mps + x.mps)
}

data class Temperature(val celsius: Double) {
    override fun toString(): String = "$celsius \u2103"
}

data class Direction(val degrees: Double) {
    override fun toString(): String = "$degrees degrees"
}

data class UvIndex(val uv: Double) {
    override fun toString(): String = "$uv"
}

data class Distance(val meters: Double) {
    override fun toString(): String = if (meters < 1) "${meters * 1000} mm"
    else if (meters < 1000) "${meters} m"
    else "${meters / 1000} km"

    val feet get() = meters * 3.2808399
    val nauticalMiles get() = meters * 0.000539956803

    operator fun times(x: Number) = Distance(meters = meters * x.toDouble())
    operator fun plus(x: Distance) = Distance(meters + x.meters)
}

data class Fraction(val fraction: Double) {
    override fun toString(): String = "$fraction %"
}

val Double.mps get() = Speed(mps = this)
val Int.mps get() = this.toDouble().mps

// Speed
private operator fun Number.times(s: Speed) = s * this
val Double.kmph get() = Speed(mps = this / 3.6)
val Int.kmph get() = this.toDouble().kmph
val Double.knots get() = Speed(mps = 0.51444424416 * this)
val Int.knots get() = this.toDouble().knots


// Temperature
val Double.celsius get() = Temperature(celsius = this)
val Int.celsius get() = this.toDouble().celsius

// Pressure
private operator fun Number.times(s: Pressure) = s * this
val Double.hpa get() = Pressure(hpa = this)
val Int.hpa get() = this.toDouble().hpa
val Double.pa get() = Pressure(hpa = this / 10.0)
val Int.pa get() = this.toDouble().pa

val Double.degrees get() = Direction(degrees = this)
val Int.degrees get() = this.toDouble().degrees

val Double.uv get() = UvIndex(uv = this)
val Int.uv get() = this.toDouble().uv

val Double.humidity get() = Humidity(humidity = this)
val Int.humidity get() = this.toDouble().humidity

val Double.fraction get() = Fraction(fraction = this)
val Int.fraction get() = this.toDouble().fraction

// Distance
private operator fun Number.times(m: Distance) = m * this
