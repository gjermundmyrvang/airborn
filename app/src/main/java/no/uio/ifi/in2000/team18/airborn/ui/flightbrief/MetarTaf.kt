package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import alexmaryin.metarkt.MetarParser
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetarTaf(metarTaf: MetarTaf?) = Collapsible(header = "Metar/Taf", expanded = false) {
    val clipboardManager = LocalClipboardManager.current
    val metar = metarTaf?.latestMetar
    val taf = metarTaf?.latestTaf
    val pageState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    TabRow(selectedTabIndex = pageState.currentPage) {
        Tab(selected = pageState.currentPage == 0,
            onClick = { scope.launch { pageState.animateScrollToPage(0) } },
            text = { Text("Raw") })
        Tab(selected = pageState.currentPage == 1,
            onClick = { scope.launch { pageState.animateScrollToPage(1) } },
            text = { Text("Decoded") })

    }
    HorizontalPager(state = pageState) { index ->
        when (index) {
            0 -> {
                Column {
                    Text(text = "METAR:", fontWeight = FontWeight.Bold)
                    if (metar != null) {
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
            }

            1 -> {
                if (metar != null) {
                    Card {
                        MetarDecode(metar = metar.text)
                    }

                } else {
                    Text("No METAR available")
                }
            }
        }

    }
}

@Composable
fun MetarDecode(metar: String) {
    val parser = MetarParser.current()
    val decode = parser.parse(metar)
    Column {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .border(2.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {

                Text(text = "Metar", fontWeight = FontWeight.Bold)
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Station name: ")
                    }
                    append(decode.station.toString())
                })
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Report time: ")
                    }
                    val localTime = LocalDateTime.parse(decode.reportTime.toString())
                    val zonedDateTimeGMT = ZonedDateTime.of(localTime, ZoneId.of("GMT"))
                    val zoneIdNorway = ZoneId.of("Europe/Oslo")
                    val zonedDateTimeNorway = zonedDateTimeGMT.withZoneSameInstant(zoneIdNorway)
                    val formattedTime =
                        zonedDateTimeNorway.format(DateTimeFormatter.ofPattern("HH:mm"))
                    append("${decode.reportTime?.date} at $formattedTime local time.")
                })
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Cavok status: ")
                    }
                    append(decode.ceilingAndVisibilityOK.toString())
                })
                Spacer(modifier = Modifier.height(10.dp))
                if (decode.wind != null) {
                    val wind = decode.wind
                    Text(text = "Wind", fontWeight = FontWeight.Bold)
                    if (wind != null) {
                        when (wind.isCalm) {
                            true -> Text(text = "All clear and no winds")
                            false -> when (wind.variable) {
                                true -> Text(text = "Wind is variable at ${wind.speed} KT")
                                false -> Text(buildAnnotatedString {
                                    append("Direction: ${wind.direction}°.\n")
                                    append("Speed: ${wind.speed} ${decode.wind?.speedUnits}")
                                })
                            }
                        }
                    }
                    if (wind != null) {
                        if (wind.gusts != 0) {
                            Text(text = "Gusts: ${wind.gustsKt} ${wind.speedUnits}")
                        }
                    }
                }
                val raw = decode.raw.split(" ")
                if (raw[3].length > 4 && raw[3].contains("V")) {
                    Text(text = "Variable winds from ${raw[3].take(3)}° to ${raw[3].takeLast(3)}°")
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Visibility", fontWeight = FontWeight.Bold)
                if (decode.visibility != null) {
                    val vis = decode.visibility
                    when (vis!!.distAll) {
                        9999 -> Text(text = "Visibility: >10 km")
                        else -> {
                            val distToKm = vis.distAll!!.div(1000.0)
                            val result = floor(distToKm * 10) / 10
                            Text(text = "Visibility: $result km")
                        }
                    }
                    if (vis.byDirections.isNotEmpty()) {
                        Text(text = "Remark", fontWeight = FontWeight.Bold)
                        Text(text = "Directions: ${vis.byDirections.joinToString(", ")}")
                    }
                    if (vis.byRunways.isNotEmpty()) {
                        Text(text = "Lowest visual: ${vis.byRunways.joinToString(", ")} ${vis.distUnits}")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Weather conditions:", fontWeight = FontWeight.Bold)

                if (decode.phenomenons.isNotEmpty()) {
                    var qualifier = decode.phenomenons[0].intensity.toString()
                        .substring(0, 1) +
                            decode.phenomenons[0].intensity.toString()
                                .substring(1)
                                .lowercase(Locale.ROOT)
                    if (qualifier == "None") {
                        qualifier = "Moderate"
                    }
                    var phenomenon =
                        decode.phenomenons[0].group.joinToString(separator = " ").substring(0, 1) +
                                decode.phenomenons[0].group.joinToString(separator = " ")
                                    .substring(1).lowercase(Locale.ROOT)
                    if (phenomenon.contains("In_vicinity")) {
                        phenomenon = phenomenon.substring(12)
                        Text(text = "$qualifier $phenomenon in the vicinity")
                    } else {
                        Text(text = "$qualifier $phenomenon")
                    }

                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Clouds: ", fontWeight = FontWeight.Bold)
                val clouds = decode.clouds[0].type.toString()
                val cloudString =
                    clouds.substring(0, 1) + clouds.substring(1).lowercase(Locale.ROOT)
                when (decode.clouds[0].type.toString()) {
                    "CLEAR" -> Text(text = "Sky is clear")
                    "NIL_SIGNIFICANT" -> Text(text = "No significant clouds. Layer at ${decode.clouds[0].lowMarginFt * 100} ft")
                    else -> {
                        Text(text = "$cloudString layer at ${decode.clouds[0].lowMarginFt * 100} ft")
                    }
                }
                val cumulus = decode.clouds[0].cumulusType
                if (cumulus != null) {
                    Text(text = "Cumulus Type: " + decode.clouds[0].cumulusType)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Temperature", fontWeight = FontWeight.Bold)
                Text(text = "Air temperature: ${decode.temperature?.air} °C")
                Text(text = "Dew point: ${decode.temperature?.dewPoint} °C")

                Spacer(modifier = Modifier.height(10.dp))
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text = "Sea Level Pressure: ")
                    }
                    append("${decode.pressureQNH?.hPa.toString()} hPa")
                })
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun TestMetarDecode() {
    val test = "ENGM 080850Z 18003G10KT 150V240 9988 -VCSNTS NSC009 M08/M06 Q1005 TEMPO BKN012="
    MetarDecode(metar = test)
}
