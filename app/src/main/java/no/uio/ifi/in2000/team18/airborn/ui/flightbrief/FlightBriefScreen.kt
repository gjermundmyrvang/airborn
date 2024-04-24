package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.LocalNavController
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.AirportTabViewModel.ArrivalViewModel
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.AirportTabViewModel.DepartureViewModel
import no.uio.ifi.in2000.team18.airborn.ui.home.AirportInfoRow
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.WeatherSection
import no.uio.ifi.in2000.team18.airborn.ui.webcam.WebcamSection

@Preview(showSystemUi = true)
@Composable
fun TestFlightBrief() {
    FlightBriefScreenContent(FlightBriefViewModel.UiState(
        hasArrival = false,
        departureIcao = Icao(""),
    ), filterArrivalAirports = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightBriefScreen(
    viewModel: FlightBriefViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "AIRBORN", fontWeight = FontWeight.Bold, fontSize = 50.sp)
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Home"
                    )
                }
            })
        },
    ) { padding ->
        FlightBriefScreenContent(
            state,
            filterArrivalAirports = { viewModel.filterArrivalAirports(it) },
            modifier = Modifier.padding(padding),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlightBriefScreenContent(
    state: FlightBriefViewModel.UiState,
    filterArrivalAirports: (String) -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier = modifier) {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()

    HorizontalPager(state = pagerState, modifier = Modifier.weight(1.0F)) { index ->
        when (index) {
            0 -> DepartureAirportBriefTab()
            1 -> if (state.hasArrival) ArrivalAirportBriefTab() else ArrivalSelectionTab(state) {
                filterArrivalAirports(
                    it
                )
            }

            2 -> OverallAirportBrieftab()
        }
    }
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
}

@Composable
fun ArrivalSelectionTab(
    state: FlightBriefViewModel.UiState, filterArrivalAirports: (String) -> Unit
) {
    val airports = state.airports
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.arrivalAirportInput,
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth(),
            onValueChange = {
                filterArrivalAirports(it)
            },
            singleLine = true,
            label = { Text("Add an arrival airport") },
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
        )
        val navController = LocalNavController.current
        LazyColumn(modifier = Modifier.imePadding(), content = {
            items(airports) { airport ->
                AirportInfoRow(modifier = Modifier, airport) {
                    navController.popBackStack("home", false)
                    navController.navigate("flightBrief/${state.departureIcao}/${airport.icao.code}")
                }
            }
        })
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
        item { OffshoreMaps(state.offshoreMaps) { viewModel.initOffshoreMpas() } }
        item { GeoSatelliteImage(state.geoSatelliteImage) { viewModel.initGeosatelliteImage() } }
        item { Route(state.route) { viewModel.initRouteIsobaric() } }
    }
}

@Composable
fun AirportBriefTab(viewModel: AirportTabViewModel) {
    val state by viewModel.state.collectAsState()
    val sections: List<@Composable () -> Unit> = listOf(
        { AirportBriefHeader(state.airport) },
        { MetarTaf(state.metarTaf) { viewModel.initMetarTaf() } },
        { IsobaricData(state.isobaric) { viewModel.initIsobaric() } },
        { Turbulence(state.turbulence) { viewModel.initTurbulence() } },
        { WebcamSection(state.webcams) { viewModel.initWebcam() } },
        { WeatherSection(state.weather) { viewModel.initWeather() } },
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
fun AirportBriefHeader(airportstate: LoadingState<Airport>) = Column {
    when (airportstate) {
        is LoadingState.Loading -> Text(text = " ")
        is LoadingState.Error -> Error(
            message = airportstate.message, modifier = Modifier.padding(16.dp)
        )

        is LoadingState.Success -> AirportInfo(
            name = airportstate.value.name,
            icao = airportstate.value.icao.code,
            pos = airportstate.value.position
        )
    }
}

@Composable
fun AirportInfo(name: String, icao: String, pos: Position) = Column(
    Modifier
        .padding(16.dp)
        .fillMaxWidth(),
) {
    Text(text = icao, fontWeight = FontWeight.Bold)
    Text(text = name, fontWeight = FontWeight.Bold)
}