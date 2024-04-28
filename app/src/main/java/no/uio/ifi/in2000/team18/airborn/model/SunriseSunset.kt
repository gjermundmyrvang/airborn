package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import no.uio.ifi.in2000.team18.airborn.ui.common.ZDTAdapter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class SunriseSunset(
    val copyright: String,
    val licenseURL: String,
    val type: String,
    val geometry: Geometry,
    val welcomeWhen: When,
    val properties: SunriseSunsetProperties,
)

data class Geometry(
    val type: String, val coordinates: List<Double>,
)

data class SunriseSunsetProperties(
    val body: String,
    val sunrise: Sun,
    val sunset: Sun,
    val solarnoon: Solar,
    val solarmidnight: Solar,
)

data class Solar(
    val time: String,
    val discCentreElevation: Double,
    val visible: Boolean,
)

class ZonedDateTimeAdapter : TypeAdapter<ZonedDateTime>() {
    override fun read(reader: JsonReader) = ZonedDateTime.parse(reader.nextString())
    override fun write(writer: JsonWriter, value: ZonedDateTime) =
        writer.value(value.format(DateTimeFormatter.ISO_DATE)).let {}
}

data class Sun(
    @JsonAdapter(ZDTAdapter::class) val time: ZonedDateTime?,
    val azimuth: Double?
)

data class When(
    val interval: List<String>,
)

