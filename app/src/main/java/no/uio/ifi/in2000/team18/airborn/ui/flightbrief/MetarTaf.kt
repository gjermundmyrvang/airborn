package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            targetValue = if (rotated) 180f else 0f
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
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Text(
                            text = "RAW DATA:",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1F)
                                .fillMaxSize()
                        )
                        Icon(imageVector = rememberFlip(), contentDescription = "Flip")
                    }
                    if (metar != null) {
                        Text(
                            text = "METAR:",
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
                    Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                        DecodedMetar(metar = metar)
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
fun DecodedMetar(metar: Metar) = Column(
    modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
) {
    Row {
        Text(
            text = "DECODED",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1F)
                .fillMaxSize()
        )
        Icon(imageVector = rememberFlip(), contentDescription = "Flip")
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
                    when (it.type) {
                        CloudType.Nothing -> {
                            Text("${it.cover} clouds at ${it.height.formatAsFeet()}") // TODO: Make more human readable
                        }

                        CloudType.Unknown -> {
                            Text("${it.cover} clouds of unknown type at ${it.height}")
                        }

                        else -> {
                            Text("${it.cover} ${it.type} at ${it.height}")
                        }
                    }
                }
            }
        }

        Clouds.NCD -> Text("No clouds detected", fontWeight = FontWeight.Bold)
        Clouds.NSC -> Text("No significant clouds", fontWeight = FontWeight.Bold)
    }
}

// Create new icon
@Composable
fun rememberFlip(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "flip",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(7.875f, 34.75f)
                quadToRelative(-1.042f, 0f, -1.833f, -0.792f)
                quadToRelative(-0.792f, -0.791f, -0.792f, -1.833f)
                verticalLineTo(7.875f)
                quadToRelative(0f, -1.042f, 0.792f, -1.833f)
                quadToRelative(0.791f, -0.792f, 1.833f, -0.792f)
                horizontalLineToRelative(6.917f)
                quadToRelative(0.541f, 0f, 0.937f, 0.396f)
                reflectiveQuadToRelative(0.396f, 0.937f)
                quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                reflectiveQuadToRelative(-0.937f, 0.375f)
                horizontalLineTo(7.875f)
                verticalLineToRelative(24.25f)
                horizontalLineToRelative(6.917f)
                quadToRelative(0.541f, 0f, 0.937f, 0.375f)
                reflectiveQuadToRelative(0.396f, 0.917f)
                quadToRelative(0f, 0.583f, -0.396f, 0.958f)
                reflectiveQuadToRelative(-0.937f, 0.375f)
                close()
                moveToRelative(12.208f, 3.417f)
                quadToRelative(-0.583f, 0f, -0.958f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.959f)
                verticalLineTo(3.208f)
                quadToRelative(0f, -0.541f, 0.396f, -0.937f)
                reflectiveQuadToRelative(0.937f, -0.396f)
                quadToRelative(0.542f, 0f, 0.917f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.937f)
                verticalLineToRelative(33.625f)
                quadToRelative(0f, 0.584f, -0.375f, 0.959f)
                reflectiveQuadToRelative(-0.917f, 0.375f)
                close()
                moveTo(32.125f, 7.875f)
                horizontalLineToRelative(-0.417f)
                verticalLineTo(5.25f)
                horizontalLineToRelative(0.417f)
                quadToRelative(1.042f, 0f, 1.833f, 0.792f)
                quadToRelative(0.792f, 0.791f, 0.792f, 1.833f)
                verticalLineToRelative(0.417f)
                horizontalLineToRelative(-2.625f)
                close()
                moveToRelative(0f, 14.25f)
                verticalLineToRelative(-4.25f)
                horizontalLineToRelative(2.625f)
                verticalLineToRelative(4.25f)
                close()
                moveToRelative(0f, 12.625f)
                horizontalLineToRelative(-0.417f)
                verticalLineToRelative(-2.625f)
                horizontalLineToRelative(0.417f)
                verticalLineToRelative(-0.458f)
                horizontalLineToRelative(2.625f)
                verticalLineToRelative(0.458f)
                quadToRelative(0f, 1.042f, -0.792f, 1.833f)
                quadToRelative(-0.791f, 0.792f, -1.833f, 0.792f)
                close()
                moveToRelative(0f, -19.5f)
                verticalLineToRelative(-4.292f)
                horizontalLineToRelative(2.625f)
                verticalLineToRelative(4.292f)
                close()
                moveToRelative(0f, 13.792f)
                verticalLineTo(24.75f)
                horizontalLineToRelative(2.625f)
                verticalLineToRelative(4.292f)
                close()
                moveToRelative(-8.083f, 5.708f)
                verticalLineToRelative(-2.625f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(2.625f)
                close()
                moveToRelative(0f, -26.875f)
                verticalLineTo(5.25f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(2.625f)
                close()
            }
        }.build()
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestMetarDecode() = Column {
    Box {
        DecodedMetar(
            parseMetar(
                "ENSG 150720Z 07207KT 030V100 9999 400N R33/P2000 FEW040 SCT090 BKN100TCU 02/M02 Q1001 RMK WIND 3806FT 10015KT=",
                Instant.parse("2024-04-16T00:00:00Z")
            ).expect()
        )
    }
    Box {
        DecodedMetar(
            parseMetar(
                "ENSS 152150Z 07014KT 1100 R33/P2000 -SN VCSHRAFGSS -VCSHSNFG -VCFG +FGSQ +SQ +VCTSRASNSQ SCT005 BKN015 OVC038 M01/M02 Q1009 RMK WIND 0500FT VRB04KT="
            ).expect()
        )
    }
}