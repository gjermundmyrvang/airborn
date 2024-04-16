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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.FlightBriefViewModel.AirportUiState
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.FlightBriefViewModel.UiState
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.WeatherSection
import no.uio.ifi.in2000.team18.airborn.ui.webcam.WebcamSection

@Preview(showSystemUi = true)
@Composable
fun TestFlightBrief() {
    FlightBriefScreen()
}

@Composable
fun FlightBriefScreen(viewModel: FlightBriefViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    FlightBriefScreenContent(state = state)
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
fun FlightBriefScreenContent(state: UiState) = Column {
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
            0 -> AirportBriefTab(state.departure)
            1 -> state.arrival?.let { AirportBriefTab(it) }
            2 -> OverallInfoTab(state)
        }
    }
}

@Composable
fun AirportBriefTab(airport: AirportUiState) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    val sections: List<@Composable () -> Unit> = listOf(
        // { AirportBriefHeader(airportBrief.airport) },
        { MetarTaf(airport.metarTaf) },
        { IsobaricData(airport.isobaric) },
        { Turbulence(airport.turbulence) },
        { WebcamSection(airport.webcams) },
        { WeatherSection(airport.weather) },
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
fun OverallInfoTab(state: UiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item { Sigchart(state.sigcharts) }
    }
}




