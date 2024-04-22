package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseMetar
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Cav
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.CloudType
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Clouds
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarWindDirection
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Rvr
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.VisibilityDistance
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon
import java.time.ZoneId

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetarTaf(state: LoadingState<MetarTaf?>, initMetar: () -> Unit) =
    LazyCollapsible(header = "Metar/Taf", value = state, onExpand = initMetar) { metarTaf ->
        val clipboardManager = LocalClipboardManager.current
        val metar = metarTaf?.latestMetar
        val taf = metarTaf?.latestTaf
        var rotated by remember {
            mutableStateOf(false)
        }
        val rotate by animateFloatAsState(
            targetValue = if (rotated) 180f else 0f,
            animationSpec = tween(500)
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotate
                    cameraDistance = 8 * density
                }
                .clickable { rotated = !rotated },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            if (!rotated) {
                Column( modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()) {
                    Row {
                        Text(text = "RAW:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1F)
                            )
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = "Flip")
                    }

                    if (metar != null) {
                        Text(text = "METAR:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = metar.text, modifier = Modifier.clickable {
                            clipboardManager.setText(
                                AnnotatedString(metar.text)
                            )
                        })
                    } else {
                        Text("No METAR available")
                    }
                    if (taf != null) {
                        Text(text = "TAF:", fontWeight = FontWeight.Bold)
                        Text(text = taf.text, modifier = Modifier.clickable {
                            clipboardManager.setText(
                                AnnotatedString(taf.text)
                            )
                        })
                    } else {
                        Text("No Taf available")
                    }
                }
            } else {
                if (metar != null) {
                    Column {
                        DecodedMetar(metar = metar, rotate)
                    }
                } else {
                    Text("No METAR available")
                }
            }
        }
    }

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.format(format: String) = format(LocalDateTime.Format { byUnicodePattern(format) })

@Composable
fun DecodedMetar(metar: Metar, rotate: Float) = Column(
    modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
        .graphicsLayer {
            rotationY = rotate
        }
) {

    Row {
        Text(
            text = "DECODED",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1F))
        Icon(imageVector = Icons.Outlined.Info, contentDescription = "Flip")
    }
    Text(
        text = "METAR",
        fontWeight = FontWeight.Bold
    )
    Row {
        Text("Station Name: ", fontWeight = FontWeight.Bold)
        Text("${metar.station}")
    }
    metar.instant?.let {
        Row {
            val timeZone = ZoneId.systemDefault().toKotlinTimeZone()
            val localTime = it.toLocalDateTime(timeZone)
            val formattedTime = localTime.format("dd/MM-yy HH:mm")
            Text("Time: ", fontWeight = FontWeight.Bold)
            Text(
                "$formattedTime local time"
            )
        }
    }
    // TODO: Report Time
    when (metar.cav) {
        Cav.OK -> Text("Ceiling and visibility OK", fontWeight = FontWeight.Bold)
        is Cav.Info -> {
            MetarClouds(metar.cav.clouds)
            MetarVisibility(metar.cav.visibility)
            MetarRvrList(metar.cav.rvrs)
            metar.cav.weatherPhenomena.forEach { phenomenon ->
                Row { Text("Phenomenon: ", fontWeight = FontWeight.Bold); Text("$phenomenon") }
            }
        }
    }
    Row {
        Text("Wind: ", fontWeight = FontWeight.Bold)
        val wind = metar.wind.first
        if (wind.direction is MetarWindDirection.Constant) {
            RotatableArrowIcon(wind.direction.direction, iconSize = 16.dp)
        }
        Text("${wind.direction.formatAsDegrees(0)} ${wind.speed.formatAsKnots(1)}") // TODO: Arrow pointing in wind direction
        wind.gustSpeed?.let { gusts -> Text(" (gusts: ${gusts.formatAsKnots(1)})") }
    }
    metar.wind.second?.let { variableDirection ->
        Row {
            Text("Variable Wind Direction: ", fontWeight = FontWeight.Bold)
            Text("${variableDirection.first} to ${variableDirection.second}")
        }
    }

    Row {
        Text("Altimeter Setting: ", fontWeight = FontWeight.Bold)
        Text("${metar.altimeterSetting}")
    }

    Row {
        Text("Temperature: ", fontWeight = FontWeight.Bold)
        Text(metar.temperatures.first.toString())
    }
    Row {
        Text("Dew point: ", fontWeight = FontWeight.Bold)
        Text(metar.temperatures.second.toString())
    }
    if (metar.rest != "") {
        Row {
            Text("Remarks: ", fontWeight = FontWeight.Bold)
            Text(metar.rest)
        }
    }
}


@Composable
fun MetarRvrList(rvrs: List<Rvr>) {
    rvrs.forEach { rvr ->
        Row {
            Text("RVR for runway ${rvr.runway}: ", fontWeight = FontWeight.Bold)
            Text("${rvr.visibility} ${rvr.trend ?: ""}")
        }
    }
}

@Composable
fun MetarVisibility(visibility: Pair<VisibilityDistance, List<Pair<VisibilityDistance, Direction>>?>) {
    val directional = visibility.second
    Row {
        Text("Visibility: ", fontWeight = FontWeight.Bold)
        Text("${visibility.first}")
        if (directional == null) {
            Text(" (no directional variation reporting capability)")
            return
        }
    }

    directional?.forEach { vis ->
        Row {
            Text(
                "Visibility towards ${vis.second.formatAsPrincipal()}: ",
                fontWeight = FontWeight.Bold
            )
            Text("${vis.first}")
        }
    }
}

@Composable
fun MetarClouds(clouds: Clouds) {
    when (clouds) {
        is Clouds.Layers -> {
            if (clouds.layers.isEmpty()) {
                Text("No Cloud information")
            }
            clouds.layers.forEach {
                Row {
                    Text("Cloud Layer: ", fontWeight = FontWeight.Bold)
                    if (it.type == CloudType.Nothing) {
                        Text("${it.cover} clouds at ${it.height.formatAsFeet()}") // TODO: Make more human readable
                    } else if (it.type == CloudType.Unknown) {
                        Text("${it.cover} clouds of unknown type at ${it.height}")
                    } else {
                        Text("${it.cover} ${it.type} at ${it.height}")
                    }
                }
            }
        }

        Clouds.NCD -> Text("No clouds detected", fontWeight = FontWeight.Bold)
        Clouds.NSC -> Text("No significant clouds", fontWeight = FontWeight.Bold)
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestMetarDecode() = Column {
    var rotated by remember {
        mutableStateOf(false)
    }
    val rotate by animateFloatAsState(
            targetValue = if (rotated) 180f else 0f,
            animationSpec = tween(500)
        )
    DecodedMetar(
        parseMetar(
            "ENSG 150720Z 07207KT 030V100 9999 400N R33/P2000 FEW040 SCT090 BKN100TCU 02/M02 Q1001 RMK WIND 3806FT 10015KT=",
            Instant.parse("2024-04-16T00:00:00Z")
        ).expect(),
        rotate
    )
    DecodedMetar(
        parseMetar(
            "ENSS 152150Z 07014KT 1100 R33/P2000 -SN VCSHRAFGSS -VCSHSNFG -VCFG +FGSQ +SQ +VCTSRASNSQ SCT005 BKN015 OVC038 M01/M02 Q1009 RMK WIND 0500FT VRB04KT=").expect(),
        rotate
        )
}
