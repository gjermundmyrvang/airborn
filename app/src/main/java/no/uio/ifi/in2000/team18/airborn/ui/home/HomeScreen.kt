package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    MapBoxHomeScreen()
    Column(modifier=Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally){
        Spacer(modifier = Modifier.weight(2F))
        Button(
            onClick = {
                scope.launch {
                    bottomSheetScaffoldState.bottomSheetState.partialExpand()
                }
            }
        ) {
            Row() {
                Text("Select airport")
            }
        }
    }
    BottomSheetScaffold(
        modifier = modifier
            .padding(16.dp),
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AirportSelection(modifier = modifier.fillMaxWidth(), viewModel = viewModel)
            }
        },
        sheetPeekHeight = 300.dp,
        sheetShadowElevation = 5.dp,
        sheetContainerColor = MaterialTheme.colorScheme.primaryContainer,
        content = {
            Text(
                text = "AIRBORN", fontWeight = FontWeight.Bold, fontSize = 80.sp
            )
        },
    )
}


@Composable
private fun AirportSelection(
    modifier: Modifier,
    viewModel: HomeViewModel,
) {
    val state by viewModel.state.collectAsState()
    val airports = state.airports
    val navController = LocalNavController.current
    var departureSelected by remember { mutableStateOf(false) }
    var arrivalSelected by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = state.departureAirportInput,
        modifier = modifier,
        onValueChange = {
            viewModel.filterDepartureAirports(it)
        },
        singleLine = true,
        label = { Text("Departure airport") },
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
    )
    OutlinedTextField(
        value = state.arrivalAirportInput,
        modifier = modifier,
        onValueChange = {
            viewModel.filterArrivalAirports(it)
        },
        singleLine = true,
        enabled = departureSelected,
        label = { Text("Arrival airport") },
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
    )
    Button(
        onClick = {
            when (departureSelected && arrivalSelected) {
                true -> navController.navigate("flightBrief/${state.departureAirportInput}/${state.arrivalAirportInput}")
                false -> navController.navigate("flightBrief/${state.departureAirportInput}/null")
            }
        }, enabled = departureSelected, modifier = modifier, shape = RoundedCornerShape(8.dp)
    ) {
        Text("Generate brief")
    }
    LazyColumn(modifier = Modifier.imePadding(), content = {
        items(airports) { airport ->
            AirportInfoRow(item = airport) { clickedAirport ->
                keyboardController?.hide()
                if (!departureSelected) {
                    departureSelected = true
                    viewModel.selectDepartureAirport(clickedAirport.icao.code)
                } else {
                    arrivalSelected = true
                    viewModel.selectArrivalAirport(clickedAirport.icao.code)
                }
            }
        }
    })
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
        Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "Location")
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.name, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    text = item.icao.code, color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${item.position.latitude}N/${item.position.longitude}E",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
}

@Preview
@Composable
fun HomeScreenPreview() {
//    val mockViewModel = HomeViewModel(AirportDataSource()).apply { filterAirports("") }
//    HomeScreen(viewModel = mockViewModel)
}