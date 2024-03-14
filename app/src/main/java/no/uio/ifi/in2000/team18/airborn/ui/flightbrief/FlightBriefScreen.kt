package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Preview
@Composable
fun TestFlightBrief() {
    FlightBrief()
}

@Composable
fun FlightBrief(viewModel: FlightBriefViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    val flightbrief = state.flightbrief

    when (flightbrief) {
        is LoadingState.Loading -> {
            // TODO: Show spinner
        }

        is LoadingState.Error -> {
            // TODO: Handle error
        }

        is LoadingState.Success ->
            LazyColumn(
                modifier = Modifier,
            ) {
                item {
                    Collapsible(header = "Metar/Taf", expanded = true) {
                        Column {
                            Text(text = "METAR: ${flightbrief.value.departure.metarTaf?.latestMetar}")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "TAF: ENGM 111100Z 1112/1212 03008KT 9999 BKN020=")
                        }
                    }
                }
                item {
                    Collapsible(header = "Sigchart") {
                        SubcomposeAsyncImage(
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://aa031pag95akoa22u.api.met.no/weatherapi/sigcharts/2.0/?area=norway&time=2024-03-11T18%3A00%3A00Z")
                                .setHeader("User-Agent", "Team18").crossfade(500).build(),
                            loading = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(30.dp),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        strokeWidth = 1.dp
                                    )
                                }
                            },
                            contentDescription = "Image of ..."
                        )
                    }
                }
                item {
                    Collapsible(header = "Turbulence") {
                        Column {
                            SubcomposeAsyncImage(
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth,
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://aa031pag95akoa22u.api.met.no/weatherapi/turbulence/2.0/?icao=ENBN&time=2024-03-12T12%3A00%3A00Z&type=map")
                                    .setHeader("User-Agent", "Team18").crossfade(500).build(),
                                loading = {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(30.dp),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            strokeWidth = 1.dp
                                        )
                                    }
                                },
                                contentDescription = "Image of ..."
                            )
                            Divider(
                                modifier = Modifier.padding(all = 5.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            SubcomposeAsyncImage(
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth,
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://aa031pag95akoa22u.api.met.no/weatherapi/turbulence/2.0/?icao=ENBN&time=2024-03-12T12%3A00%3A00Z&type=cross_section")
                                    .setHeader("User-Agent", "Team18").crossfade(500).build(),
                                loading = {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(30.dp),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            strokeWidth = 1.dp
                                        )
                                    }
                                },
                                contentDescription = "Image of ..."
                            )
                        }
                    }
                }
            }
    }
}

@Composable
fun Collapsible(
    header: String, expanded: Boolean = false, content: @Composable BoxScope.() -> Unit
) {
    var open by remember {
        mutableStateOf(expanded)
    }
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 300, easing = LinearOutSlowInEasing
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = header, fontSize = 22.sp)
            IconButton(onClick = { open = !open }) {
                Icon(
                    imageVector = if (open) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier.size(30.dp),
                    contentDescription = if (open) "Show less" else "Show more"
                )
            }
        }
        if (open) {
            Box(
                modifier = Modifier.padding(16.dp),
                content = content,
            )
        }
        Divider(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showSystemUi = true)
@PreviewLightDark
@Composable
fun LightPreviewFlightBrief() {
    Collapsible(header = "Metar/Taf") {
        Column {
            Text(text = "METAR: ENGM 111550Z 01009KT CAVOK 00/M06 Q1019 NOSIG=")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "TAF: ENGM 111100Z 1112/1212 03008KT 9999 BKN020=")
        }
    }
}
