package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.LocalNavController
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.ui.connectivity.ConnectivityObserver
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTextFieldColors
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) = Box {
    var airportInputSelected by remember { mutableStateOf(false) }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            false,
            LocalDensity.current,
            skipHiddenState = false,
            initialValue = SheetValue.PartiallyExpanded
        )
    )
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 300.dp,
        sheetShadowElevation = 5.dp,
        sheetContainerColor = MaterialTheme.colorScheme.primaryContainer,
        sheetContent = {
            AirportSelection(modifier = modifier.padding(16.dp),
                viewModel = viewModel,
                onFocusChange = {
                    airportInputSelected = it
                    if (it) {
                        scope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    }
                })
        },
        content = {
            Map(viewModel, modifier = Modifier.fillMaxSize(), airportSelected = {
                scope.launch {
                    bottomSheetScaffoldState.bottomSheetState.partialExpand()
                }
            })
            Button(modifier = Modifier.align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.background),
                onClick = {
                    scope.launch {
                        if (airportInputSelected) {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        } else {
                            bottomSheetScaffoldState.bottomSheetState.partialExpand()
                        }
                    }
                }) {
                Text("Select Airport")
            }
        }
    )
}


@Composable
private fun AirportSelection(
    modifier: Modifier, viewModel: HomeViewModel, onFocusChange: (Boolean) -> Unit = {}
) = Column(
    modifier = modifier
) {
    val state by viewModel.state.collectAsState()
    val searchResults = state.searchResults
    val navController = LocalNavController.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var departureFocused by remember { mutableStateOf(false) }
    var arrivalFocused by remember { mutableStateOf(false) }

    val departureAirport = state.departureAirport
    val arrivalAirport = state.arrivalAirport


    Box(
        Modifier.fillMaxWidth()
    ) {
        Column {
            OutlinedTextField(
                value = state.departureAirportInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { departureFocused = it.isFocused },
                onValueChange = { viewModel.filterDepartureAirports(it) },
                colors = AirbornTextFieldColors,
                singleLine = true,
                label = { Text("Departure airport") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.flight_takeoff
                        ),
                        contentDescription = "takeoff icon",
                        tint = MaterialTheme.colorScheme.background
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.clearDepartureInput() }) {
                        Icon(Icons.Filled.Close, contentDescription = "clear departure inputfield")
                    }
                },
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.arrivalAirportInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { arrivalFocused = it.isFocused },
                onValueChange = {
                    viewModel.filterArrivalAirports(it)
                },
                colors = AirbornTextFieldColors,
                singleLine = true,
                enabled = state.departureAirport != null,
                label = { Text("Arrival airport") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.flight_landing
                        ),
                        contentDescription = "landing icon",
                        tint = MaterialTheme.colorScheme.background
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.clearArrivalInput() }) {
                        Icon(Icons.Filled.Close, contentDescription = "clear arrival inputfield")
                    }
                },
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }),
            )
        }
        IconButton(
            onClick = {
                if (departureAirport != null || arrivalAirport != null) {
                    if (departureAirport == null) viewModel.switchToDeparture()
                    else if (arrivalAirport == null) viewModel.switchToArrival()
                    else viewModel.switchDepartureArrival()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 50.dp)
                .height(IntrinsicSize.Max)
                .background(Color(0xFF1D1D1D), RoundedCornerShape(5.dp))
                .clip(RoundedCornerShape(5.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.switch_departure_arrival),
                contentDescription = "Your icon",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }

    LaunchedEffect(arrivalFocused || departureFocused) {
        onFocusChange(arrivalFocused || departureFocused)
    }
    Spacer(modifier = Modifier.height(32.dp))

    if (!(departureFocused || arrivalFocused)) {
        Button(
            onClick = {
                navController.navigate("flightBrief/${state.departureAirportInput}/${state.arrivalAirport?.icao?.code ?: "null"}")
            },
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            enabled = state.departureAirport != null && state.networkStatus == ConnectivityObserver.Status.Available,
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
        ) { Text("Go to brief") }
    }

    val configuration = LocalConfiguration.current
    if (!(departureFocused || arrivalFocused)) return@Column
    LazyColumn(modifier = Modifier.height(configuration.screenHeightDp.dp)) {
        items(searchResults) { airport ->
            AirportInfoRow(item = airport) { clickedAirport ->
                keyboardController?.hide()
                if (departureFocused) {
                    viewModel.selectDepartureAirport(clickedAirport)
                    focusManager.clearFocus(true)
                } else {
                    viewModel.selectArrivalAirport(clickedAirport)
                    focusManager.clearFocus(true)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestTextField() {
    AirbornTheme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .onFocusChanged { },
                onValueChange = { },
                colors = AirbornTextFieldColors,
                singleLine = true,
                maxLines = 1,
                label = { Text("Departure airport") },
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Close, contentDescription = "clear departure inputfield")
                    }
                },
                keyboardActions = KeyboardActions(onDone = { }),
            )
            OutlinedTextField(
                value = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .onFocusChanged { },
                onValueChange = { },
                enabled = false,
                colors = AirbornTextFieldColors,
                singleLine = true,
                maxLines = 1,
                label = { Text("Arrival airport") },
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Close, contentDescription = "clear departure inputfield")
                    }
                },
                keyboardActions = KeyboardActions(onDone = { }),
            )
            Button(
                onClick = {},
                colors = ButtonColors(
                    containerColor = Color(0xFFFB9B50),
                    contentColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(8.dp),
            ) { Text("Go to brief") }
            Button(
                onClick = {},
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                enabled = false,
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(8.dp),
            ) { Text("Go to brief") }
        }
    }
}

@Composable
fun AirportInfoRow(
    modifier: Modifier = Modifier,
    item: Airport,
    onItemClick: (Airport) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .clickable { onItemClick(item) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = "Location",
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.name,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = item.icao.code, color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${item.position.latitude}N/${item.position.longitude}E",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary)
}