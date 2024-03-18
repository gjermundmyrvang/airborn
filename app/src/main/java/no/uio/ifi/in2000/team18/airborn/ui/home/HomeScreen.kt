package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.LocalNavController
import no.uio.ifi.in2000.team18.airborn.data.AirportDataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val uistate by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDropdown by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(expanded = showDropdown, onExpandedChange = {
                showDropdown = it
            }) {
                OutlinedTextField(
                    value = uistate.airportInput,
                    onValueChange = {
                        viewModel.filterAirports(it)
                        showDropdown = true
                    },
                    singleLine = true,
                    label = { Text("Start Destination") },
                    modifier = Modifier.menuAnchor(),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
                ExposedDropdownMenu(expanded = showDropdown, onDismissRequest = {
                    showDropdown = false
                }) {
                    uistate.airports.forEach { airport ->
                        DropdownMenuItem(
                            {
                                Column {
                                    Text(airport.icao.code)
                                    Text(airport.name)
                                }
                            },
                            onClick = {
                                showDropdown = false
                                viewModel.selectAirport(airport.icao.code)
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

@Preview
@Composable
fun HomeScreenPreview() {
//    val mockViewModel = HomeViewModel(AirportDataSource()).apply { filterAirports("") }
//    HomeScreen(viewModel = mockViewModel)
}