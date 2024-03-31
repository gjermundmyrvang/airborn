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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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
                LazyColumn(modifier = modifier.imePadding(), content = {
                    items(airports) { airport ->
                        AirportInfoRow(item = airport) { clickedAirport ->
                            viewModel.selectAirport(clickedAirport.icao.code)
                            scope.launch {
                                val id = viewModel.generateFlightbrief()
                                navController.navigate("flightbrief/$id")
                            }
                        }
                    }
                })
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