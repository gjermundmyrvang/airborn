package no.uio.ifi.in2000.team18.airborn.ui.common

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZDTAdapter : TypeAdapter<ZonedDateTime>() {
    override fun write(writer: JsonWriter, value: ZonedDateTime) {
        writer.value(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
    }

    override fun read(reader: JsonReader): ZonedDateTime {
        val dateString = reader.nextString()
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }
}

fun ZonedDateTime.monthDayHourMinute(): String {
    return "${
        this.month.name.lowercase().replaceFirstChar { it.uppercase() }
    } ${this.dayOfMonth}, ${this.hourMinute()}"
}

fun ZonedDateTime.hourMinute(): String {
    val hour = if (this.hour > 9) {
        "${this.hour}"
    } else "0${this.hour}"

    val minute = if (this.minute > 10) {
        "${this.minute}"
    } else "${this.minute}0"

    return "$hour:$minute"
}

fun ZonedDateTime.toSystemZoneOffset(): ZonedDateTime {
    return this.withZoneSameInstant(ZoneId.systemDefault())
}