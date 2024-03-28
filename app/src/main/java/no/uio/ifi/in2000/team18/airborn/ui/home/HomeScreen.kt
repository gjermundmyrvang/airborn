package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.LocalNavController
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val uistate by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDepartureDropdown by remember { mutableStateOf(false) }
    var showArrivalDropdown by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(80.dp))
            Text(text = "Departure from:")
            ExposedDropdownMenuBox(expanded = showDepartureDropdown, onExpandedChange = {
                showDepartureDropdown = it
            }) {
                OutlinedTextField(
                    value = uistate.departureAirportInput,
                    onValueChange = {
                        viewModel.filterDepartureAirports(it)
                        showDepartureDropdown = true
                    },
                    singleLine = true,
                    label = { Text("Start Destination") },
                    modifier = Modifier.menuAnchor(),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
                ExposedDropdownMenu(expanded = showDepartureDropdown, onDismissRequest = {
                    showDepartureDropdown = false
                }) {
                    uistate.departureAirports.forEach { airport ->
                        DropdownMenuItem(
                            {
                                Column {
                                    Text(airport.icao.code)
                                    Text(airport.name)
                                }
                            },
                            onClick = {
                                showDepartureDropdown = false
                                viewModel.selectDepartureAirport(airport.icao.code)
                                keyboardController?.hide()
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
            Text(text = "Arriving at:")
            ExposedDropdownMenuBox(expanded = showArrivalDropdown, onExpandedChange = {
                showArrivalDropdown = it
            }) {
                OutlinedTextField(
                    value = uistate.arrivalAirportInput,
                    onValueChange = {
                        viewModel.filterArrivalAirports(it)
                        showArrivalDropdown = true
                    },
                    singleLine = true,
                    label = { Text("End Destination") },
                    modifier = Modifier.menuAnchor(),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    }),
                )
                ExposedDropdownMenu(expanded = showArrivalDropdown, onDismissRequest = {
                    showArrivalDropdown = false
                }) {
                    uistate.arrivalAirports.forEach { airport ->
                        DropdownMenuItem(
                            {
                                Column {
                                    Text(airport.icao.code)
                                    Text(airport.name)
                                }
                            },
                            onClick = {
                                showArrivalDropdown = false
                                viewModel.selectArrivalAirport(airport.icao.code)
                                keyboardController?.hide()
                            },
                        )
                    }
                }
            }
            val scope = rememberCoroutineScope()
            Spacer(modifier = Modifier.weight(3f))
            Button(
                onClick = {
                    // Generate flightbrief
                    scope.launch {
                        val id = viewModel.generateFlightbrief()
                        navController.navigate("flightbrief/$id")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff62c4c3))
            ) {
                Text("Generer flightbrief", fontSize = 18.sp)
            }
        }
    }
}
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen2(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val airports = state.airports
    val navController = LocalNavController.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        BottomSheetScaffold(scaffoldState = bottomSheetScaffoldState, sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = state.airportInput,
                    onValueChange = {
                        viewModel.filterAirports(it)
                    },
                    singleLine = true,
                    label = { Text("Departure airport") },
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    }),
                )
                LazyColumn(
                    modifier = modifier
                        .imePadding(),
                    content = {
                        items(airports) { airport ->
                            AirportInfoRow(item = airport) { clickedAirport ->
                                viewModel.selectAirport(clickedAirport.icao.code)
                                scope.launch {
                                    val id = viewModel.generateFlightbrief()
                                    navController.navigate("flightbrief/$id")
                                }
                            }
                        }
                    }
                )
            }
        }, sheetPeekHeight = 300.dp, content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "AIRBORN", fontWeight = FontWeight.Bold, fontSize = 80.sp
                )
            }
        })
    }
}

@Composable
private fun AirportInfoRow(
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
        Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "Location")
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.name, color = Color.Black, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = item.icao.code, color = Color.Black
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${item.position.latitude}N/${item.position.longitude}E",
                    color = Color.Black
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
//    val mockViewModel = HomeViewModel(AirportDataSource()).apply { filterAirports("") }
//    HomeScreen(viewModel = mockViewModel)
}