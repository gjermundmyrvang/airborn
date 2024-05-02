package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.LocalNavController
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.AirportTabViewModel.ArrivalViewModel
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.AirportTabViewModel.DepartureViewModel
import no.uio.ifi.in2000.team18.airborn.ui.home.AirportInfoRow
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.WeatherSection
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTextFieldColors
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTheme
import no.uio.ifi.in2000.team18.airborn.ui.webcam.WebcamSection

@Preview(showSystemUi = true)
@Composable
fun TestFlightBrief() {
    FlightBriefScreenContent(FlightBriefViewModel.UiState(
        arrivalIcao = null,
        departureIcao = Icao(""),
    ), filterArrivalAirports = {}, onSelectArrival = {}, clearArrivalInput = {})
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
                Image(
                    painter = painterResource(id = R.drawable.newtextlogo2),
                    contentDescription = "",
                    modifier = Modifier.size(200.dp)
                )
            }, navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack(route = "home", inclusive = false)
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("flightBrief/${state.departureIcao}/${state.arrivalIcao ?: "null"}")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "refresh",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
                    actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor
                )
            )
        }, containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { padding ->
        FlightBriefScreenContent(
            state,
            filterArrivalAirports = { viewModel.filterArrivalAirports(it) },
            onSelectArrival = { viewModel.selectArrivalAirport(it) },
            clearArrivalInput = { viewModel.clearArrivalInput() },
            modifier = Modifier.padding(padding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TesFlighBriefScreen() {
    AirbornTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(text = "AIRBORN", fontWeight = FontWeight.Bold, fontSize = 50.sp)
                }, navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Home"
                        )
                    }
                })
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyCollapsible(
                    header = "TEST",
                    value = LoadingState.Loading,
                    onExpand = { /*TODO*/ },
                    expanded = true
                ) {
                    Text(text = "Yo")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlightBriefScreenContent(
    state: FlightBriefViewModel.UiState,
    filterArrivalAirports: (String) -> Unit,
    onSelectArrival: (String) -> Unit,
    clearArrivalInput: () -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier = modifier) {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()

    HorizontalPager(state = pagerState, modifier = Modifier.weight(1.0F)) { index ->
        when (index) {
            0 -> DepartureAirportBriefTab()
            1 -> if (state.hasArrival) ArrivalAirportBriefTab() else ArrivalSelectionTab(state,
                filterArrivalAirports = { filterArrivalAirports(it) },
                onSelectArrival = { onSelectArrival(it) },
                clearArrivalInput = { clearArrivalInput() })

            2 -> OverallAirportBrieftab()
        }
    }
    // TODO change color of the indicator underneath
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        indicator = {},
        divider = {},
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Tab(
            selected = pagerState.currentPage == 0,
            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
            text = { Text("Departure") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.flight_takeoff),
                    contentDescription = "Flight takeoff"
                )
            },
            selectedContentColor = MaterialTheme.colorScheme.background,
            unselectedContentColor = Color.LightGray
        )
        Tab(
            selected = pagerState.currentPage == 1,
            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
            text = { Text("Arrival") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.flight_landing),
                    contentDescription = "Flight takeoff"
                )
            },
            selectedContentColor = MaterialTheme.colorScheme.background,
            unselectedContentColor = Color.LightGray
        )
        Tab(
            selected = pagerState.currentPage == 2,
            onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
            text = { Text("Overall") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.connecting_airports_icon),
                    contentDescription = "Flight takeoff"
                )
            },
            selectedContentColor = MaterialTheme.colorScheme.background,
            unselectedContentColor = Color.LightGray
        )
    }
}

@Composable
fun ArrivalSelectionTab(
    state: FlightBriefViewModel.UiState,
    filterArrivalAirports: (String) -> Unit,
    onSelectArrival: (String) -> Unit,
    clearArrivalInput: () -> Unit
) {
    val airports = state.airports
    val keyboardController = LocalSoftwareKeyboardController.current
    var enabled by remember { mutableStateOf(false) }
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
            colors = AirbornTextFieldColors,
            singleLine = true,
            label = { Text("Add an arrival airport") },
            trailingIcon = {
                IconButton(onClick = {
                    enabled = false
                    clearArrivalInput()
                }) {
                    Icon(Icons.Filled.Close, contentDescription = "clear arrival inputfield")
                }
            },
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
        )
        val navController = LocalNavController.current
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                navController.popBackStack("home", false)
                navController.navigate("flightBrief/${state.departureIcao}/${state.arrivalAirportInput}")
            },
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(8.dp),
        ) { Text("Add arrival") }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.imePadding(), content = {
            items(airports) { airport ->
                AirportInfoRow(modifier = Modifier, airport) {
                    keyboardController?.hide()
                    enabled = true
                    onSelectArrival(it.icao.code)
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
        if (state.hasArrival) {
            item {
                WindsAloftRoute(state.routeIsobaric,
                    initRouteIsobaric = { viewModel.initRouteIsobaric() },
                    onUpdateIsobaric = { distance, time ->
                        viewModel.changeRouteIsobaric(
                            distance, time
                        )
                    })
            }
        }
        item { RadarAnimations(state.radarAnimations) { viewModel.initRadarAnimations() } }
        item { GeoSatelliteImage(state.geoSatelliteImage) { viewModel.initGeosatelliteImage() } }
        item { OffshoreMaps(state.offshoreMaps) { viewModel.initOffshoreMaps() } }
        if (state.isIgaRoute) { // Only show this composable if selected departure and arrival is a route
            item {
                RouteForecast(state = state.routeForecast) {
                    viewModel.initRoute()
                }
            }
        }
    }
}

@Composable
fun AirportBriefTab(viewModel: AirportTabViewModel) {
    val state by viewModel.state.collectAsState()
    val sections: List<@Composable () -> Unit> = listOf(
        { AirportBriefHeader(state.airport) },
        { Sundata(state.sun) },
        { MetarTaf(state.metarTaf) { viewModel.initMetarTaf() } },
        { WebcamSection(state.webcams) { viewModel.initWebcam() } },
        { WeatherSection(state.weather) { viewModel.initWeather() } },
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(sections) { section ->
            section()
        }
        if (state.hasTurbulence) {
            item { Turbulence(state.turbulence) { viewModel.initTurbulence() } }
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
            airport = airportstate.value
        )
    }
}

@Composable
fun Sundata(sun: LoadingState<Sun?>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SunComposable(
            modifier = Modifier.padding(end = 10.dp), sun = sun, header = ""
        )
    }
}

@Composable
fun AirportInfo(airport: Airport) = Column(
    Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 16.dp)
        .fillMaxWidth(),
) {
    val hasSeperator = airport.name.contains(",")
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!hasSeperator) {
            Column {
                Text(text = airport.icao.code, color = MaterialTheme.colorScheme.secondary)
                Text(text = airport.name)
            }
        } else {
            Column {
                Text(text = airport.icao.code, color = MaterialTheme.colorScheme.secondary)
                Text(text = airport.name.substringBefore(","), fontWeight = FontWeight.Bold)
            }
            Text("/", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Text(text = airport.name.substringAfter(","), fontWeight = FontWeight.Bold)
        }
    }
}