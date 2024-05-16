package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime

data class Sigchart(
    val params: SigchartParameters, val updated: String, val uri: String
)

data class SigchartParameters(
    val area: Area,
    @JsonAdapter(ZonedDateTimeAdapter::class) val time: ZonedDateTime
)

enum class Area {
    @SerializedName("nordic")
    Nordic,
    @SerializedName("norway")
    Norway
}
