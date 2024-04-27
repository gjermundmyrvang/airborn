package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTimeAdapter

data class Sigchart(
    val params: SigchartParameters, val updated: String, val uri: String
)

data class SigchartParameters(
    val area: Area,
    @JsonAdapter(DateTimeAdapter::class) val time: DateTime
)

enum class Area {
    nordic, norway
}