package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.SigchartParameters
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.AirportBrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.FlightBrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.Weathersection

@Preview(showSystemUi = true)
@Composable
fun TestFlightBrief() {
    FlightBriefScreen()
}

@Composable
fun FlightBriefScreen(viewModel: FlightBriefViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    when (val flightBrief = state.flightBrief) {
        is LoadingState.Loading -> LoadingScreen()
        is LoadingState.Error -> Text("Error", color = Color.Red)
        is LoadingState.Success -> FlightBriefScreenContent(flightBrief = flightBrief.value)
    }
}

@Composable
fun LoadingScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "LOADING...")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlightBriefScreenContent(flightBrief: FlightBrief) = Column {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()
    TabRow(selectedTabIndex = pagerState.currentPage) {
        Tab(selected = pagerState.currentPage == 0,
            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
            text = { Text("Departure") })
        Tab(selected = pagerState.currentPage == 1,
            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
            text = { Text("Arrival") })
        Tab(selected = pagerState.currentPage == 2,
            onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
            text = { Text("Overall") })
    }
    HorizontalPager(state = pagerState, modifier = Modifier.weight(1.0F)) { index ->
        when (index) {
            0 -> DepartureBriefTab(flightBrief.departure)
            1 -> flightBrief.arrival?.let { ArrivalBriefTab(it) }
            2 -> OverallInfoTab(flightBrief = flightBrief)
        }
    }
}

@Composable
fun DepartureBriefTab(airportBrief: AirportBrief) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    item {
        val airport = airportBrief.airport.name
        Column {
            Text(
                text = airport,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 50.sp,
            )
        }
    }
    item {
        Collapsible(header = "Metar/Taf", expanded = true) {
            Column {
                Text(text = "METAR:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestMetar}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "TAF:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestTaf}")
            }
        }
    }
    item {
        Collapsible(header = "Isobaric data") {
            Column(modifier = Modifier.fillMaxWidth()) {
                // data from isobaric layers, includes height TODO: a table or chart would be nice
                Text(text = "${airportBrief.isobaric?.time}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("height", fontWeight = FontWeight.Bold)
                    Text("temperature", fontWeight = FontWeight.Bold)
                    Text("pressure", fontWeight = FontWeight.Bold)
                }
                airportBrief.isobaric?.data?.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("%.0f m".format(it.height))
                        Text("%.1f c".format(it.temperature - 273.15))
                        Text("%.0f hPa".format(it.pressure))
                    }
                }
            }
        }
    }
    item {
        Collapsible(header = "Turbulence") {
            Column {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.map?.last()?.uri)
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
                    contentDescription = "Image of turbulence map"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(all = 5.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.crossSection?.last()?.uri)
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
                    contentDescription = "Image of turbulence cross section"
                )
            }
        }
    }
    item {
        Collapsible(header = "Weather", padding = 0.dp) {
            Weathersection(weather = airportBrief.weather)
        }
    }
}


@Composable
fun ArrivalBriefTab(airportBrief: AirportBrief) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    item {
        val airport = airportBrief.airport.name
        Column {
            Text(
                text = airport,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 50.sp,
            )
        }
    }
    item {
        Collapsible(header = "Metar/Taf", expanded = true) {
            Column {
                Text(text = "METAR:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestMetar}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "TAF:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestTaf}")
            }
        }
    }
    item {
        Collapsible(header = "Isobaric data") {
            Column(modifier = Modifier.fillMaxWidth()) {
                // data from isobaric layers, includes height TODO: a table or chart would be nice
                Text(text = "${airportBrief.isobaric?.time}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("height", fontWeight = FontWeight.Bold)
                    Text("temperature", fontWeight = FontWeight.Bold)
                    Text("pressure", fontWeight = FontWeight.Bold)
                }
                airportBrief.isobaric?.data?.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("%.0f m".format(it.height))
                        Text("%.1f c".format(it.temperature - 273.15))
                        Text("%.0f hPa".format(it.pressure))
                    }
                }
            }
        }
    }
    item {
        Collapsible(header = "Turbulence") {
            Column {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.map?.last()?.uri)
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
                HorizontalDivider(
                    modifier = Modifier.padding(all = 5.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.crossSection?.last()?.uri)
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
    item {
        Collapsible(header = "Weather", padding = 0.dp) {
            Weathersection(weather = airportBrief.weather)
        }
    }
}


@Composable
fun OverallInfoTab(flightBrief: FlightBrief) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Collapsible(header = "Sigchart") {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(flightBrief.sigchart.uri)
                        .setHeader("User-Agent", "Team18")
                        .crossfade(500)
                        .build(),
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
                    contentDescription = "Image of sigchart. Updated at ${flightBrief.sigchart.updated}"
                )
            }
        }
    }
}


@Composable
fun Collapsible(
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    header: String,
    expanded: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    var open by rememberSaveable {
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
                modifier = modifier.padding(padding),
                content = content,
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LightPreviewFlightBrief() {
    FlightBriefScreenContent(
        flightBrief = FlightBrief(
            departure = AirportBrief(
                airport = Airport(
                    icao = Icao("ENGM"), name = "Gardermoen", Position(0.0, 0.0)
                ),
                metarTaf = MetarTaf(listOf(Metar("Hello")), listOf()),
                turbulence = null,
                isobaric = null,
                weather = listOf()
            ), arrival = null, altArrivals = listOf(), sigchart = Sigchart(
                params = SigchartParameters(area = Area.Norway, time = ""),
                updated = "",
                uri = "",
            )
        )
    )
}
