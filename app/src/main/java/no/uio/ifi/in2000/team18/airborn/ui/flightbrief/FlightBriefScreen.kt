package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
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
            0 -> AirportBriefTab(flightBrief.departure)
            1 -> flightBrief.arrival?.let { AirportBriefTab(it) }
            2 -> OverallInfoTab(flightBrief = flightBrief)
        }
    }
}

@Composable
fun AirportBriefTab(airportBrief: AirportBrief) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    val sections: List<@Composable () -> Unit> = listOf(
        { AirportBriefHeader(airportBrief.airport) },
        { MetarTaf(airportBrief.metarTaf) },
        { IsobaricData(airportBrief.isobaric) },
        { Turbulence(airportBrief.turbulence) },
        { Weathersection(airportBrief.weather) },
    )
    items(sections) { section ->
        section()
    }
}

@Composable
fun AirportBriefHeader(airport: Airport) = Column {
    Text(
        text = airport.name,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 50.sp,
    )
}

@Composable
fun OverallInfoTab(flightBrief: FlightBrief) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item { Collapsible(header = "Sigchart") { Sigchart(flightBrief.sigchart) } }
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
                metarTaf = MetarTaf(listOf(Metar("")), listOf()),
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


