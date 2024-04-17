package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.AirportTabViewModel.ArrivalViewModel
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.AirportTabViewModel.DepartureViewModel
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.WeatherSection
import no.uio.ifi.in2000.team18.airborn.ui.webcam.WebcamSection

@Preview(showSystemUi = true)
@Composable
fun TestFlightBrief() {
    FlightBriefScreen()
}

@Composable
fun FlightBriefScreen() {
    FlightBriefScreenContent()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlightBriefScreenContent() = Column {
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
            0 -> DepartureAirportBriefTab()
            1 -> ArrivalAirportBriefTab()
            2 -> OverallAirportBrieftab()
        }
    }
}

@Composable
fun DepartureAirportBriefTab(
    viewModel: DepartureViewModel = hiltViewModel(),
) = AirportBriefTab(viewModel = viewModel)

@Composable
fun ArrivalAirportBriefTab(
    viewModel: ArrivalViewModel = hiltViewModel(),
) = AirportBriefTab(viewModel = viewModel)

@Composable
fun OverallAirportBrieftab(
    viewModel: FlightBriefViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item { Sigchart(state.sigcharts) { viewModel.initSigchart() } }
    }
}

@Composable
fun AirportBriefTab(viewModel: AirportTabViewModel) {
    val state by viewModel.state.collectAsState()
    val sections: List<@Composable () -> Unit> = listOf(
        // { AirportBriefHeader(airportBrief.airport) },
        { MetarTaf(state.metarTaf) { viewModel.initMetarTaf() } },
        { IsobaricData(state.isobaric) { /*viewModel.initIsobaric()*/ } },
        { Turbulence(state.turbulence) { viewModel.initTurbulence() } },
        { WebcamSection(state.webcams) { /*viewModel.initWebcam()*/ } },
        { WeatherSection(state.weather) { /*viewModel.initWeather()*/ } },
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(sections) { section ->
            section()
        }
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




