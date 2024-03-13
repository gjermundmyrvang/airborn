package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {

    val uistate by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = uistate.airportInput,
                onValueChange = {
                    viewModel.filterAirports(it.text)
                },
                label = { Text("Start Destination") },
                modifier = Modifier.fillMaxWidth(),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (uistate.showDropdown) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = {
                        coroutineScope.launch {
                            viewModel._state.value =
                                viewModel._state.value.copy(showDropdown = false)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uistate.airports.forEach { airport ->
                        DropdownMenuItem({ Text(airport) }, onClick = {
                            coroutineScope.launch {
                                viewModel.filterAirports(airport)
                                viewModel._state.value =
                                    viewModel._state.value.copy(showDropdown = false)
                            }
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.weight(2f))
            Button(onClick = {},
                modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)){
                Text("Generer flightbrief", fontSize = 18.sp)
            }
        }
    }
}
@Preview
@Composable
fun HomeScreenPreview(){
    val navController = rememberNavController()
    val mockViewModel = HomeViewModel().apply { filterAirports("")}
    HomeScreen(viewModel = mockViewModel)
}