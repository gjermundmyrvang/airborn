package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime


data class OffshoreMap(
    @SerializedName("endpoint") val endpoint: String,
    @SerializedName("params") val params: OffshoreParams,
    @SerializedName("labels") val labels: Labels,
    @SerializedName("updated") @JsonAdapter(DateTimeAdapter::class) val updated: DateTime,
    @SerializedName("uri") val uri: String
)

data class Labels(
    val area: String
)

data class OffshoreParams(
    val area: String,
    @JsonAdapter(DateTimeAdapter::class) val time: DateTime
)

class DateTimeAdapter : TypeAdapter<DateTime>() {
    override fun write(writer: JsonWriter, value: DateTime) = writer.value(value.isoDateTime).let {}
    override fun read(reader: JsonReader): DateTime = DateTime(reader.nextString())
}
