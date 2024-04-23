package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTimeAdapter

data class WebcamResponse(
    val total: Long,
    val webcams: List<Webcam>
)

data class Webcam(
    val title: String,
    val viewCount: Long,
    val webcamID: Long,
    val status: String,
    @JsonAdapter(DateTimeAdapter::class) val lastUpdatedOn: DateTime,
    val images: Images
)

data class Images(
    val current: Current,
    val sizes: Sizes,
    val daylight: Current
)

data class Current(
    val icon: String,
    val thumbnail: String,
    val preview: String
)

data class Sizes(
    val icon: Icon,
    val thumbnail: Icon,
    val preview: Icon
)

data class Icon(
    val width: Long,
    val height: Long
)