package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseMetar
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.degrees
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Cav
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.CloudType
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Clouds
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Rvr
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.VisibilityDistance
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetarTaf(state: LoadingState<MetarTaf?>, initMetar: () -> Unit) =
    LazyCollapsible(header = "Metar/Taf", value = state, onExpand = initMetar) { metarTaf ->
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
                            DecodedMetar(metar = metar)
                        }

                    } else {
                        Text("No METAR available")
                    }
                }
            }

        }
    }

@Composable
fun DecodedMetar(metar: Metar) = Column(
    modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
) {

    Text(text = "Metar", fontWeight = FontWeight.Bold)
    Row {
        Text("Station Name: ", fontWeight = FontWeight.Bold)
        Text("${metar.station}")
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
        Text("${wind.direction} ${wind.speed}") // TODO: Arrow pointing in wind direction
        wind.gustSpeed?.let { gusts -> Text(" (gusts: ${gusts})") }
    }
    metar.wind.second?.let { variableDirection ->
        Row {
            Text("Variable Wind Direction: ", fontWeight = FontWeight.Bold)
            Text("${variableDirection.first.degrees} to ${variableDirection.second.degrees}")
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
            Text("Not Decoded: ", fontWeight = FontWeight.Bold)
            Text(metar.rest)
        }
    }
}


@Composable
fun MetarRvrList(rvrs: List<Rvr>) {
    rvrs.forEach { rvr ->
        Row {
            Text("RVR for runway ${rvr.runway}: ", fontWeight = FontWeight.Bold)
            Text("${rvr.visibility} ${rvr.trend}")
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
                        Text("${it.cover} clouds at ${it.height}") // TODO: Make more human readable
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
    DecodedMetar(parseMetar("ENSG 150720Z 07007KT 030V100 9999 400N R33/P2000 FEW040 SCT090 BKN100TCU 02/M02 Q1001 RMK WIND 3806FT 10015KT=").expect())
    DecodedMetar(parseMetar("ENSS 152150Z 07014KT 1100 R33/P2000 -SN VCSHFGSS -VCSHSNFG -VCFG +FGSQ +SQ SCT005 BKN015 OVC038 M01/M02 Q1009 RMK WIND 0500FT VRB04KT=").expect())
}
