package no.uio.ifi.in2000.team18.airborn.ui.common

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DateTime(val isoDateTime: String) {
    fun toLocalDateTime(): LocalDateTime {
        val utcDateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME)
        return utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    val date
        get(): String = toLocalDateTime().toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd. MMM, yyyy", Locale.ENGLISH))
    val month
        get(): String = toLocalDateTime().toLocalDate()
            .format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH))
    val day
        get(): String = toLocalDateTime().toLocalDate()
            .format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))

    val dayNumber
        get(): String = toLocalDateTime().toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd", Locale.ENGLISH))

    val dayNumberMonth = "$dayNumber. $month"

    val dayNumberMonthTime = "$dayNumber. $month. $time"

    val dayMonthHour = "$day $month $time"

    val time
        get(): String = toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
}

class DateTimeAdapter : TypeAdapter<DateTime>() {
    override fun write(writer: JsonWriter, value: DateTime) = writer.value(value.isoDateTime).let {}
    override fun read(reader: JsonReader): DateTime = DateTime(reader.nextString())
}