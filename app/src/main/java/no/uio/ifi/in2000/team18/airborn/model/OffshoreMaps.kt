package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime


data class OffshoreMap(
    @SerializedName("endpoint") val endpoint: String,
    @SerializedName("params") val params: OffshoreParams,
    @SerializedName("labels") val labels: Labels,
    @SerializedName("updated") @JsonAdapter(ZonedDateTimeAdapter::class) val updated: ZonedDateTime,
    @SerializedName("uri") val uri: String
)

data class Labels(
    val area: String
)

data class OffshoreParams(
    val area: String,
    @JsonAdapter(ZonedDateTimeAdapter::class) val time: ZonedDateTime
)

